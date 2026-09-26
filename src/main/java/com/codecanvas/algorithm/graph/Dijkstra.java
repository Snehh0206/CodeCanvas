package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class Dijkstra implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();
        int comparisons = 0;

        for (GraphNode node : graph.getNodes()) {
            distances.put(node.getId(), Integer.MAX_VALUE);
        }
        distances.put(startNodeId, 0);

        steps.add(new GraphStep(null, null, null, new ArrayList<>(visited),
                new HashMap<>(distances), comparisons, 0, "Start at " + startNodeId, 1));
        steps.add(new GraphStep(null, null, null, new ArrayList<>(visited),
                new HashMap<>(distances), comparisons, 0, "distance[" + startNodeId + "] = 0", 2));

        while (visited.size() < graph.getNodes().size()) {
            steps.add(new GraphStep(null, null, null, new ArrayList<>(visited),
                    new HashMap<>(distances), comparisons, 0,
                    visited.size() + " of " + graph.getNodes().size() + " nodes visited so far", 3));

            String current = null;
            int currentBest = Integer.MAX_VALUE;

            for (GraphNode node : graph.getNodes()) {
                if (!visited.contains(node.getId()) && distances.get(node.getId()) < currentBest) {
                    currentBest = distances.get(node.getId());
                    current = node.getId();
                }
            }

            if (current == null) break;

            visited.add(current);
            steps.add(new GraphStep(current, null, null, new ArrayList<>(visited),
                    new HashMap<>(distances), comparisons, 0, "Visiting closest node: " + current, 5));

            for (GraphEdge edge : graph.getEdgesFrom(current)) {
                comparisons++;
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                        new HashMap<>(distances), comparisons, 0,
                        "Checking edge " + edge.getFrom() + " -> " + edge.getTo(), 6));

                int newDist = distances.get(current) + edge.getWeight();
                boolean improved = newDist < distances.get(edge.getTo());
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                        new HashMap<>(distances), comparisons, 0,
                        improved ? "New distance (" + newDist + ") is shorter" : "No improvement found", 7));

                if (improved) {
                    distances.put(edge.getTo(), newDist);
                    steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                            new HashMap<>(distances), comparisons, 0,
                            "Updated distance to " + edge.getTo() + ": " + newDist, 8));
                }
            }
        }
        return steps;
    }

    @Override
    public String getName() { return "Dijkstra"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V^2) simple version, O((V+E) log V) with a priority queue"; }
}