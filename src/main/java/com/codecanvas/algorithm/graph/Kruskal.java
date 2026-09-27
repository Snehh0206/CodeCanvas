package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class Kruskal implements GraphAlgorithm {

    private final Map<String, String> parent = new HashMap<>();

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        Map<String, Integer> distances = new HashMap<>(); // used here to show "connected weight" per node
        Map<String, String> predecessors = new HashMap<>();
        int comparisons = 0;
        int relaxations = 0;

        for (GraphNode node : graph.getNodes()) {
            parent.put(node.getId(), node.getId());
            distances.put(node.getId(), Integer.MAX_VALUE);
        }

        List<GraphEdge> sortedEdges = new ArrayList<>(graph.getEdges());
        sortedEdges.sort(Comparator.comparingInt(GraphEdge::getWeight));

        steps.add(new GraphStep(null, null, null, List.of(), new HashMap<>(distances),
                new HashMap<>(predecessors), 0, 0, "Edges sorted by weight, ascending", 1));

        for (GraphEdge edge : sortedEdges) {
            comparisons++;
            steps.add(new GraphStep(null, edge.getFrom(), edge.getTo(), List.of(),
                    new HashMap<>(distances), new HashMap<>(predecessors), comparisons, relaxations,
                    "Considering edge " + edge.getFrom() + " -> " + edge.getTo() + " (weight " + edge.getWeight() + ")", 3));

            if (find(edge.getFrom()).equals(find(edge.getTo()))) {
                steps.add(new GraphStep(null, edge.getFrom(), edge.getTo(), List.of(),
                        new HashMap<>(distances), new HashMap<>(predecessors), comparisons, relaxations,
                        "Skipped — would form a cycle", 4));
                continue;
            }

            union(edge.getFrom(), edge.getTo());
            predecessors.put(edge.getTo(), edge.getFrom());
            distances.put(edge.getTo(), edge.getWeight());
            relaxations++;
            steps.add(new GraphStep(null, edge.getFrom(), edge.getTo(), List.of(),
                    new HashMap<>(distances), new HashMap<>(predecessors), comparisons, relaxations,
                    "Added to MST: " + edge.getFrom() + " -> " + edge.getTo(), 5));
        }
        return steps;
    }

    private String find(String node) {
        if (!parent.get(node).equals(node)) {
            parent.put(node, find(parent.get(node)));
        }
        return parent.get(node);
    }

    private void union(String a, String b) {
        parent.put(find(a), find(b));
    }

    @Override
    public String getName() { return "Kruskal's Algorithm"; }

    @Override
    public String getTheoreticalComplexity() { return "O(E log E)"; }
}