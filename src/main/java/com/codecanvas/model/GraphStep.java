package com.codecanvas.model;

import java.util.List;
import java.util.Map;

public class GraphStep {
    private final String currentNodeId;
    private final String activeEdgeFrom;
    private final String activeEdgeTo;
    private final List<String> visitedNodes;
    private final Map<String, Integer> distances;
    private final int comparisons;
    private final int relaxations;
    private final String description;

    public GraphStep(String currentNodeId, String activeEdgeFrom, String activeEdgeTo,
                     List<String> visitedNodes, Map<String, Integer> distances,
                     int comparisons, int relaxations, String description) {
        this.currentNodeId = currentNodeId;
        this.activeEdgeFrom = activeEdgeFrom;
        this.activeEdgeTo = activeEdgeTo;
        this.visitedNodes = visitedNodes;
        this.distances = distances;
        this.comparisons = comparisons;
        this.relaxations = relaxations;
        this.description = description;
    }

    public String getCurrentNodeId() { return currentNodeId; }
    public String getActiveEdgeFrom() { return activeEdgeFrom; }
    public String getActiveEdgeTo() { return activeEdgeTo; }
    public List<String> getVisitedNodes() { return visitedNodes; }
    public Map<String, Integer> getDistances() { return distances; }
    public int getComparisons() { return comparisons; }
    public int getRelaxations() { return relaxations; }
    public String getDescription() { return description; }
}