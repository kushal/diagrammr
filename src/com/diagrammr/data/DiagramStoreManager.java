package com.diagrammr.data;


public class DiagramStoreManager {

  private static final DiagramStore instance = new AppEngineStore();
  
  public static DiagramStore get() {
    return instance;
  }
}
