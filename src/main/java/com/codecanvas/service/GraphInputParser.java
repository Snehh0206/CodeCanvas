package com.codecanvas.service;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;

public class GraphInputParser {

    public static Graph parse(int vertexCount, String edgesText) {
        Graph graph = new Graph();

        double centerX = 200, centerY = 150, radius = 120;
        for (int i = 0; i < vertexCount; i++) {
            double angle = 2 * Math.PI * i / vertexCount;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            graph.addNode(new GraphNode(String.valueOf(i), x, y));
        }

        for (String part : edgesText.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            String[] pieces = trimmed.split("-");
            String from = pieces[0].trim();
            String to = pieces[1].trim();
            int weight = pieces.length >= 3 ? Integer.parseInt(pieces[2].trim()) : 1;

            validateNodeExists(from, vertexCount);
            validateNodeExists(to, vertexCount);

            graph.addEdge(new GraphEdge(from, to, weight));
        }

        return graph;
    }

    private static void validateNodeExists(String nodeId, int vertexCount) {
        int id;
        try {
            id = Integer.parseInt(nodeId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Vertex \"" + nodeId + "\" is not a valid number");
        }
        if (id < 0 || id >= vertexCount) {
            throw new IllegalArgumentException(
                    "Edge references vertex " + id + ", but you only have vertices 0 to " + (vertexCount - 1));
        }
    }
}