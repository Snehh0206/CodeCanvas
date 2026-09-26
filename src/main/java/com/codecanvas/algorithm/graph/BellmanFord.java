package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class BellmanFord implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>();
        int relaxations = 0;

        for (GraphNode node : graph.getNodes()) {
            distances.put(node.getId(), Integer.MAX_VALUE);
        }
        distances.put(startNodeId, 0);

        steps.add(new GraphStep(startNodeId, null, null, List.of(), new HashMap<>(distances),
                0, relaxations, "Start at " + startNodeId, 1));
        steps.add(new GraphStep(startNodeId, null, null, List.of(), new HashMap<>(distances),
                0, relaxations, "distance[" + startNodeId + "] = 0", 2));

        int nodeCount = graph.getNodes().size();

        for (int i = 1; i < nodeCount; i++) {
            steps.add(new GraphStep(null, null, null, List.of(), new HashMap<>(distances),
                    0, relaxations, "Pass " + i + " of " + (nodeCount - 1), 3));

            boolean changedThisPass = false;

            for (GraphEdge edge : graph.getEdges()) {
                if (distances.get(edge.getFrom()) == Integer.MAX_VALUE) continue;

                steps.add(new GraphStep(edge.getFrom(), edge.getFrom(), edge.getTo(), List.of(),
                        new HashMap<>(distances), 0, relaxations,
                        "Checking edge " + edge.getFrom() + " -> " + edge.getTo(), 4));

                int newDist = distances.get(edge.getFrom()) + edge.getWeight();
                boolean improved = newDist < distances.get(edge.getTo());
                steps.add(new GraphStep(edge.getFrom(), edge.getFrom(), edge.getTo(), List.of(),
                        new HashMap<>(distances), 0, relaxations,
                        improved ? "Improvement found" : "No improvement", 5));

                if (improved) {
                    distances.put(edge.getTo(), newDist);
                    relaxations++;
                    changedThisPass = true;
                    steps.add(new GraphStep(edge.getFrom(), edge.getFrom(), edge.getTo(), List.of(),
                            new HashMap<>(distances), 0, relaxations,
                            "Relaxed " + edge.getTo() + " to " + newDist, 6));
                }
            }
            if (!changedThisPass) break;
        }
        return steps;
    }

    @Override
    public String getName() { return "Bellman-Ford"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V * E)"; }
}