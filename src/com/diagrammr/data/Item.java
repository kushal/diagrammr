package com.diagrammr.data;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Represents opaque persisted JSON except for its ACL, timestamp, and primary
 * key.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Item {
  
  @PrimaryKey
  @Persistent
  private String name;
  
  @Persistent
  private String contents;
  
  @Persistent
  private Text contents2;

  @Persistent
  private List<String> owners;

  @Persistent
  private Date lastEdited;
  
  public Item() {}
  
  public Item(String name, String json, List<String> owners) {
    this.name = name;
    this.contents2 = new Text(json);
    this.owners = owners;
    this.lastEdited = new Date();
  }

  public String getName() {
    return name;
  }

  public List<String> getOwners() {
    return owners;
  }

  public String getContent() {
    if (contents2 != null) {
      return contents2.getValue();
    }
    // Legacy data
    return contents;
  }

  public Date getLastEdited() {
    return lastEdited;
  }
}
