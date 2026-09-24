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
                0, relaxations, "Start at " + startNodeId));

        int nodeCount = graph.getNodes().size();

        for (int i = 1; i < nodeCount; i++) {
            boolean changedThisPass = false;

            for (GraphEdge edge : graph.getEdges()) {
                if (distances.get(edge.getFrom()) == Integer.MAX_VALUE) continue;

                steps.add(new GraphStep(edge.getFrom(), edge.getFrom(), edge.getTo(), List.of(),
                        new HashMap<>(distances), 0, relaxations,
                        "Checking edge " + edge.getFrom() + " -> " + edge.getTo()));

                int newDist = distances.get(edge.getFrom()) + edge.getWeight();
                if (newDist < distances.get(edge.getTo())) {
                    distances.put(edge.getTo(), newDist);
                    relaxations++;
                    changedThisPass = true;
                    steps.add(new GraphStep(edge.getFrom(), edge.getFrom(), edge.getTo(), List.of(),
                            new HashMap<>(distances), 0, relaxations,
                            "Relaxed " + edge.getTo() + " to " + newDist));
                }
            }
            if (!changedThisPass) break; // no more improvements possible, stop early
        }
        return steps;
    }

    @Override
    public String getName() { return "Bellman-Ford"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V * E)"; }
}