package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class BFS implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        for (GraphNode node : graph.getNodes()) distances.put(node.getId(), Integer.MAX_VALUE);
        distances.put(startNodeId, 0);
        queue.add(startNodeId);
        visited.add(startNodeId);

        steps.add(new GraphStep(startNodeId, null, null, new ArrayList<>(visited),
                new HashMap<>(distances), new HashMap<>(predecessors), 0, 0, "Start BFS at " + startNodeId, 1));

        int comparisons = 0;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            steps.add(new GraphStep(current, null, null, new ArrayList<>(visited),
                    new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0, "Dequeued " + current, 2));

            for (GraphEdge edge : graph.getEdgesFrom(current)) {
                comparisons++;
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                        new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0,
                        "Checking neighbor " + edge.getTo(), 3));

                if (!visited.contains(edge.getTo())) {
                    visited.add(edge.getTo());
                    distances.put(edge.getTo(), distances.get(current) + 1);
                    predecessors.put(edge.getTo(), current);
                    queue.add(edge.getTo());
                    steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                            new HashMap<>(distances), new HashMap<>(predecessors), comparisons, 0,
                            "Visiting " + edge.getTo() + " for the first time", 4));
                }
            }
        }
        return steps;
    }

    @Override
    public String getName() { return "BFS"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V + E)"; }
}