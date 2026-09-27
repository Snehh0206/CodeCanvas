package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class DFS implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (GraphNode node : graph.getNodes()) distances.put(node.getId(), Integer.MAX_VALUE);
        distances.put(startNodeId, 0);

        steps.add(new GraphStep(startNodeId, null, null, new ArrayList<>(visited),
                new HashMap<>(distances), new HashMap<>(predecessors), 0, 0, "Start DFS at " + startNodeId, 1));

        dfsVisit(graph, startNodeId, visited, distances, predecessors, steps, new int[]{0});
        return steps;
    }

    private void dfsVisit(Graph graph, String current, Set<String> visited, Map<String, Integer> distances,
                          Map<String, String> predecessors, List<GraphStep> steps, int[] comparisons) {
        visited.add(current);
        steps.add(new GraphStep(current, null, null, new ArrayList<>(visited),
                new HashMap<>(distances), new HashMap<>(predecessors), comparisons[0], 0, "Visiting " + current, 2));

        for (GraphEdge edge : graph.getEdgesFrom(current)) {
            comparisons[0]++;
            steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                    new HashMap<>(distances), new HashMap<>(predecessors), comparisons[0], 0,
                    "Exploring edge to " + edge.getTo(), 3));

            if (!visited.contains(edge.getTo())) {
                distances.put(edge.getTo(), distances.get(current) + 1);
                predecessors.put(edge.getTo(), current);
                steps.add(new GraphStep(current, edge.getFrom(), edge.getTo(), new ArrayList<>(visited),
                        new HashMap<>(distances), new HashMap<>(predecessors), comparisons[0], 0,
                        "Diving deeper into " + edge.getTo(), 4));
                dfsVisit(graph, edge.getTo(), visited, distances, predecessors, steps, comparisons);
            }
        }
    }

    @Override
    public String getName() { return "DFS"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V + E)"; }
}