package com.diagrammr.data;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.json.JSONException;


public class AppEngineStore implements DiagramStore {

  private PersistenceManagerFactory pmFactory;

  public AppEngineStore() {
    pmFactory = JDOHelper.getPersistenceManagerFactory("transactions-optional");
  }
  
  @Override
  public DiagramData getDiagram(String key, String email) throws IOException {
    PersistenceManager pm = pmFactory.getPersistenceManager();
    try {
      Item persisted = pm.getObjectById(Item.class, key);
      DiagramData data = toDiagramData(persisted);
      data.checkViewAcl(email);
      return data;
    } catch (JSONException e) {
      throw new IOException(e);
    } catch (AclException e) {
      throw new IOException(e);
    } finally {
      pm.close();
    }
  }

  private DiagramData toDiagramData(Item persisted)
      throws JSONException {
    DiagramData data = DiagramData.fromJSON(persisted.getContent());
    data.setOwnersUnchecked(persisted.getOwners());
    return data;
  }

  @Override
  public void updateDiagram(String key, String email, DiagramData data) throws IOException {
    PersistenceManager pm = pmFactory.getPersistenceManager();
    try {
      // Retrieve first to check ACL (TODO: this is wasteful, restructure)
      Item persisted = pm
          .getObjectById(Item.class, key);
      DiagramData currentData = toDiagramData(persisted);
      currentData.checkEditAcl(email);

      // Then save
      Item newPersisted = new Item(key, data.toJSON(),
          data.getOwners());
      pm.makePersistent(newPersisted);
    } catch (JSONException e) {
      throw new IOException(e);
    } catch (AclException e) {
      throw new IOException(e);
    } finally {
      pm.close();
    }
  }

  @Override
  public void createDiagram(String key, String email) throws IOException {
    PersistenceManager pm = pmFactory.getPersistenceManager();
    try {
      // Make sure ID isn't taken
      try {
        pm.getObjectById(Item.class, key);
        throw new IOException("Already exists");
      } catch (JDOObjectNotFoundException e) {
      }

      // Then save
      DiagramData data = DiagramData.createNew(email);
      Item newPersisted = new Item(key, data.toJSON(),
          data.getOwners());
      pm.makePersistent(newPersisted);
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  @Override
  public void deleteDiagram(String key, String email) throws IOException {
    PersistenceManager pm = pmFactory.getPersistenceManager();
    try {
      // Retrieve first to check ACL
      Item persisted = pm
          .getObjectById(Item.class, key);
      DiagramData currentData = toDiagramData(persisted);
      currentData.checkEditAcl(email);

      pm.deletePersistent(persisted);
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (AclException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, DiagramData> getDiagrams(String email) throws IOException {
    Map<String, DiagramData> data = new LinkedHashMap<String, DiagramData>();
    PersistenceManager pm = pmFactory.getPersistenceManager();
    try {
      Query query = pm.newQuery(Item.class);
      query.setFilter("owners.contains(ownerParam)");
      query.setOrdering("lastEdited desc");
      query.declareParameters("String ownerParam");
      List<Item> results = (List<Item>) query.execute(email);
      for (Item diagram : results) {
        // TODO: timestamp?
        data.put(diagram.getName(), toDiagramData(diagram));
      }
      return data;
    } catch (JSONException e) {
      throw new IOException(e);
    } finally {
      pm.close();
    }
  }
}
