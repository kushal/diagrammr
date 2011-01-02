package com.diagrammr.data;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.Test;

public class DiagramDataTest {

  @Test
  public void testToJsonDefault() throws JSONException {
    DiagramData d = new DiagramData();
    String json = d.toJSON();
    assertEquals("{\"viewPermission\":\"URL\",\"title\":\"\",\"height\":300,\"layout\":\"TOP_DOWN\",\"width\":300,\"editPermission\":\"PARTICIPANTS\"}", json);
  }
}
