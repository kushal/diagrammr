package com.diagrammr.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

/**
 * Single object containing user-provided sentences and configuration as well as
 * the sentences parsed into Edges and Vertices. Note that the parsing is
 * superfluous in the AppEngine side until some feature really uses it.
 */
public class DiagramData {

  private static final Pattern SENTENCE_PATTERN = Pattern
      .compile("(\\w+?) (.*) (\\w+)\\.?");
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
 
  public static class Edge {
    private String start;
    private String end;
    private String label;

    public Edge(String start, String end, String label) {
      this.start = start;
      this.end = end;
      this.label = label;
    }

    public String getStart() {
      return start;
    }

    public String getEnd() {
      return end;
    }

    public String getLabel() {
      return label;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Edge) {
        Edge e2 = (Edge) obj;
        return e2.end.equals(this.end) &&
          e2.start.equals(this.start) &&
          e2.label.equals(this.label);
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      // TODO Auto-generated method stub
      return super.hashCode();
    }
  }
  
  public static enum Layout {
    TOP_DOWN,
    COLLABORATION
  };

  public static enum Permission {
    URL,
    OWNERS
  };
  
  private Collection<String> vertices = new LinkedHashSet<String>();
  private Collection<Edge> edges = new ArrayList<Edge>();
  private List<String> sentences = new ArrayList<String>();
  private Layout layout = Layout.TOP_DOWN;
  private String title = "";
  private Permission viewPermission = Permission.URL;
  private Permission editPermission = Permission.URL;
  private List<String> owners = new ArrayList<String>();
  private int[] size = new int[] { 300, 300 };

  void addEdge(String label, String start, String end) {
    vertices.add(start);
    vertices.add(end);
    Edge edge = new Edge(start, end, label);
    if (!edges.contains(edge)) {
      edges.add(edge);
    }
  }

  void rebuildGraph() {
    vertices.clear();
    edges.clear();
    ArrayList<String> sentenceCopy = new ArrayList<String>();
    sentenceCopy.addAll(sentences);
    sentences.clear();
    for (String sentence : sentenceCopy) {
      addSentence(sentence);
    }
  }

  public Collection<Edge> getEdges() {
    return edges;
  }

  public Collection<String> getVertices() {
    return vertices;
  }

  public List<String> getSentences() {
    return sentences;
  }

  public Layout getLayout() {
    return layout;
  }

  public void addSentence(String sentence) {
    sentences.add(sentence);
  }
  
  public void setLayout(Layout layout) {
    this.layout = layout;
  }
  
  public void deleteSentence(String sentence) {
    sentences.remove(sentence);
    rebuildGraph();
  }
  
  public String getTitle() {
    return title;
  }
  
  public String getDisplayTitle() {
    if (!title.equals("")) {
      return title;
    }
    if (sentences.size() > 0) {
      String derivedTitle = sentences.get(0);
      if (derivedTitle.length() > 30) {
        derivedTitle = derivedTitle.substring(0, 25);
      } else {
        derivedTitle += " ";
      }
      return derivedTitle + "...";
    }
    return "Untitled";
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Permission getViewPermission() {
    return viewPermission;
  }

  public void setViewPermission(Permission viewPermission) {
    this.viewPermission = viewPermission;
  }

  public Permission getEditPermission() {
    return editPermission;
  }

  public void setEditPermission(Permission editPermission) {
    this.editPermission = editPermission;
  }

  public void setSize(int[] size) {
    this.size = size;
  }

  public int[] getSize() {
    return size;
  }

  public List<String> getOwners() {
    if (owners == null) {
      return new ArrayList<String>();
    }
    return owners;
  }

  public void moveUp(String sentence) {
    int newIndex = sentences.indexOf(sentence) - 1;
    if (newIndex > -1) {
      sentences.remove(sentence);
      sentences.add(newIndex, sentence);
    }
  }

  public void moveDown(String sentence) {
    int newIndex = sentences.indexOf(sentence) + 1;
    if (newIndex < sentences.size()) {
      sentences.remove(sentence);
      sentences.add(newIndex, sentence);      
    }
  }

  public void changeLayout() {
    if (layout == Layout.COLLABORATION) {
      layout = Layout.TOP_DOWN;
    } else {
      layout = Layout.COLLABORATION;
    }
  }

  public void checkViewAcl(String email) throws AclException {
    if (getViewPermission().equals(DiagramData.Permission.OWNERS)
        && !getOwners().contains(email.toLowerCase())) {
      throw new AclException();
    }
  }

  public void checkEditAcl(String email) throws AclException {
    if (getEditPermission().equals(DiagramData.Permission.OWNERS)
        && !getOwners().contains(email.toLowerCase())) {
      throw new AclException();
    }
  }

  void setOwnersUnchecked(List<String> owners) {
    this.owners = owners;
  }
  
  public void setOwners(List<String> owners) {
    // Sanity checks: validate addresses, make sure we
    // are not removing the last person from an owners-only doc,
    // and make sure we are not adding owners to a doc with no
    // owners. TODO: Report errors back
    if (this.owners.size() == 0) {
      throw new IllegalArgumentException("Can't add owner to unowned diagram");
    }
    for (String owner: owners) {
      if (!EMAIL_PATTERN.matcher(owner.trim().toLowerCase()).matches()) {
        throw new IllegalArgumentException("Invalid email address " + owner);
      }
    }
    if (owners.size() == 0 &&
        (this.getViewPermission() == Permission.OWNERS ||
         this.getEditPermission() == Permission.OWNERS)) {
      throw new IllegalArgumentException("Need at least one owner");
    }
    this.owners = new ArrayList<String>();
    for (String owner: owners) {
      this.owners.add(owner.trim().toLowerCase());
    }
  }

  public String toJSON() throws JSONException {
    return toJSON(false);
  }
  
  public String toJSON(boolean includeOwners) throws JSONException {
    JSONObject json = new JSONObject();
    json.put("layout", getLayout().toString());
    json.put("width", getSize()[0]);
    json.put("height", getSize()[1]);

    json.put("title", getTitle().toString());
    json.put("editPermission", getEditPermission().toString());
    json.put("viewPermission", getViewPermission().toString());

    for (String sentence : getSentences()) {
      json.append("sentences", sentence);
    }

    if (includeOwners) {
      for (String owner : getOwners()) {
        json.append("owners", owner);
      }
    }
    
    return json.toString();
  }
  
  public static DiagramData fromJSON(String s) throws JSONException {
    JSONObject json = new JSONObject(s);
    DiagramData data = new DiagramData();
    
    try {
      data.setSize(new int[]{ json.getInt("width"), json.getInt("height") });
    } catch (JSONException e) {
    }

    try {
      data.setLayout(Layout.valueOf(json.getString("layout")));
    } catch (JSONException e) {
    }

    try {
      data.setTitle(json.getString("title"));
    } catch (JSONException e) {
    }

    try {
      data.setViewPermission(Permission.valueOf(json
          .getString("viewPermission")));
    } catch (JSONException e) {
    }

    try {
      data.setEditPermission(Permission.valueOf(json
          .getString("editPermission")));
    } catch (JSONException e) {
    }

    if (json.has("sentences")) {
      JSONArray sentences = json.getJSONArray("sentences");
      for (int i = 0; i < sentences.length(); i++) {
        data.addSentence(sentences.getString(i));
      }
    }
    return data;
  }

  public static void main(String[] args) {
    Matcher m = SENTENCE_PATTERN.matcher("Dogs attack Cars.");
    m.matches();
    System.err.println(m.group(1));
    System.err.println(m.group(2));
    System.err.println(m.group(3));
  }

  public static DiagramData createNew(String email) {
    DiagramData data = new DiagramData();
    if (email != null) {
      data.setOwnersUnchecked(Lists.newArrayList(email));
    }
    return data;
  }
}
