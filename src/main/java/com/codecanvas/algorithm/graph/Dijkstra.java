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
                new HashMap<>(distances), comparisons, 0, "Start at " + startNodeId));

        while (visited.size() < graph.getNodes().size()) {
            String current = null;
            int currentBest = Integer.MAX_VALUE;

            for (GraphNode node : graph.getNodes()) {
                if (!visited.contains(node.getId()) && distances.get(node.getId()) < currentBest) {
                    currentBest = distances.get(node.getId());
                    current = node.getId();
                }
            }

            if (current == null) break; // remaining nodes unreachable

            visited.add(current);
            steps.add(new GraphStep(current, null, null, new ArrayList<>(visited),
                    new HashMap<>(distances), comparisons, 0, "Visiting " + current));

            for (GraphEdge edge : graph.getEdgesFrom(current)) {
                comparisons++;
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                        new HashMap<>(distances), comparisons, 0,
                        "Checking edge " + edge.getFrom() + " -> " + edge.getTo()));

                int newDist = distances.get(current) + edge.getWeight();
                if (newDist < distances.get(edge.getTo())) {
                    distances.put(edge.getTo(), newDist);
                    steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                            new HashMap<>(distances), comparisons, 0,
                            "Updated distance to " + edge.getTo() + ": " + newDist));
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