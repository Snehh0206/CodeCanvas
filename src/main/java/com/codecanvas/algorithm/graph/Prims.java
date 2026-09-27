package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class Prims implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Set<String> inMST = new HashSet<>();
        int comparisons = 0;

        for (GraphNode node : graph.getNodes()) {
            distances.put(node.getId(), Integer.MAX_VALUE);
        }
        distances.put(startNodeId, 0);

        steps.add(new GraphStep(null, null, null, new ArrayList<>(inMST),
                new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0, "Start MST at " + startNodeId, 1));

        while (inMST.size() < graph.getNodes().size()) {
            String current = null;
            int currentBest = Integer.MAX_VALUE;

            for (GraphNode node : graph.getNodes()) {
                if (!inMST.contains(node.getId()) && distances.get(node.getId()) < currentBest) {
                    currentBest = distances.get(node.getId());
                    current = node.getId();
                }
            }

            if (current == null) break;

            inMST.add(current);
            steps.add(new GraphStep(current, null, null, new ArrayList<>(inMST),
                    new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0,
                    "Adding " + current + " to the MST", 3));

            for (GraphEdge edge : graph.getEdgesFrom(current)) {
                comparisons++;
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(inMST),
                        new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0,
                        "Checking edge " + edge.getFrom() + " -> " + edge.getTo() + " (weight " + edge.getWeight() + ")", 4));

                if (!inMST.contains(edge.getTo()) && edge.getWeight() < distances.get(edge.getTo())) {
                    distances.put(edge.getTo(), edge.getWeight());
                    predecessors.put(edge.getTo(), current);
                    steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(inMST),
                            new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0,
                            "Cheaper connection found to " + edge.getTo() + ": weight " + edge.getWeight(), 5));
                }
            }
        }
        return steps;
    }

    @Override
    public String getName() { return "Prim's Algorithm"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V^2) simple version, O(E log V) with a priority queue"; }
}