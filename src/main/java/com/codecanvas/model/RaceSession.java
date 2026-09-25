package com.codecanvas.model;

public class RaceSession {

    private static final RaceSession instance = new RaceSession();

    private String category; // "Sorting" or "Graph"
    private String algorithm1Name;
    private String algorithm2Name;
    private boolean custom1;
    private boolean custom2;
    private int[] customValues1;
    private int[] customValues2;
    private boolean customGraph;
    private int raceVertexCount;
    private String edges1Text;
    private String edges2Text;

    private RaceSession() {}

    public static RaceSession getInstance() { return instance; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAlgorithm1Name() { return algorithm1Name; }
    public void setAlgorithm1Name(String name) { this.algorithm1Name = name; }

    public String getAlgorithm2Name() { return algorithm2Name; }
    public void setAlgorithm2Name(String name) { this.algorithm2Name = name; }

    public boolean isCustom1() { return custom1; }
    public void setCustom1(boolean custom1) { this.custom1 = custom1; }

    public boolean isCustom2() { return custom2; }
    public void setCustom2(boolean custom2) { this.custom2 = custom2; }

    public int[] getCustomValues1() { return customValues1; }
    public void setCustomValues1(int[] values) { this.customValues1 = values; }

    public int[] getCustomValues2() { return customValues2; }
    public void setCustomValues2(int[] values) { this.customValues2 = values; }

    public boolean isCustomGraph() { return customGraph; }
    public void setCustomGraph(boolean customGraph) { this.customGraph = customGraph; }

    public int getRaceVertexCount() { return raceVertexCount; }
    public void setRaceVertexCount(int count) { this.raceVertexCount = count; }

    public String getEdges1Text() { return edges1Text; }
    public void setEdges1Text(String text) { this.edges1Text = text; }

    public String getEdges2Text() { return edges2Text; }
    public void setEdges2Text(String text) { this.edges2Text = text; }
}