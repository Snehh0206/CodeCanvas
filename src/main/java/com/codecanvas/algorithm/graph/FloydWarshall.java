package com.codecanvas.algorithm.graph;

import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.model.*;

import java.util.*;

public class FloydWarshall implements GraphAlgorithm {

    @Override
    public List<GraphStep> run(Graph graph, String startNodeId) {
        List<GraphStep> steps = new ArrayList<>();
        List<String> nodeIds = new ArrayList<>();
        for (GraphNode node : graph.getNodes()) nodeIds.add(node.getId());

        Map<String, Map<String, Integer>> dist = new HashMap<>();
        for (String u : nodeIds) {
            dist.put(u, new HashMap<>());
            for (String v : nodeIds) {
                dist.get(u).put(v, u.equals(v) ? 0 : Integer.MAX_VALUE);
            }
        }
        for (GraphEdge edge : graph.getEdges()) {
            dist.get(edge.getFrom()).put(edge.getTo(), edge.getWeight());
        }

        int comparisons = 0;
        // "distances" reported per-step = distances FROM startNodeId to every node (a readable slice of the full matrix)
        steps.add(new GraphStep(null, null, null, List.of(), sliceFrom(dist, nodeIds, startNodeId),
                new HashMap<>(), 0, 0, "Initialized distance matrix", 1));

        for (String k : nodeIds) {
            for (String i : nodeIds) {
                for (String j : nodeIds) {
                    comparisons++;
                    if (dist.get(i).get(k) == Integer.MAX_VALUE || dist.get(k).get(j) == Integer.MAX_VALUE) continue;

                    int through = dist.get(i).get(k) + dist.get(k).get(j);
                    if (through < dist.get(i).get(j)) {
                        dist.get(i).put(j, through);
                        steps.add(new GraphStep(k, i, j, List.of(), sliceFrom(dist, nodeIds, startNodeId),
                                new HashMap<>(), comparisons, 0,
                                "Shorter path " + i + "->" + j + " found via " + k + ": " + through, 6));
                    }
                }
            }
        }
        steps.add(new GraphStep(null, null, null, List.of(), sliceFrom(dist, nodeIds, startNodeId),
                new HashMap<>(), comparisons, 0, "All-pairs shortest paths computed", 7));
        return steps;
    }

    private Map<String, Integer> sliceFrom(Map<String, Map<String, Integer>> dist, List<String> nodeIds, String from) {
        Map<String, Integer> slice = new HashMap<>();
        for (String node : nodeIds) slice.put(node, dist.get(from).get(node));
        return slice;
    }

    @Override
    public String getName() { return "Floyd-Warshall"; }

    @Override
    public String getTheoreticalComplexity() { return "O(V^3)"; }
}