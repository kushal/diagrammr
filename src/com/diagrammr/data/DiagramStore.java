package com.diagrammr.data;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Abstraction that does create, read, update, and delete on diagrams. The
 * provided email address is for the acting user, or null if none.
 */
public interface DiagramStore {

  public void createDiagram(String key, String email) throws IOException;
    
  public abstract DiagramData getDiagram(String key, String email) throws IOException;

  public abstract void deleteDiagram(String key, String email) throws IOException;

  public abstract void updateDiagram(String key, String email, DiagramData data) throws IOException;
  
  public Map<String, DiagramData> getDiagrams(String email) throws IOException;
}