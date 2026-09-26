package com.codecanvas.service;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;

public class GraphCaseGenerator {

    public static Graph generate(String caseType, int vertexCount) {
        Graph graph = new Graph();
        double centerX = 200, centerY = 150, radius = 120;

        for (int i = 0; i < vertexCount; i++) {
            double angle = 2 * Math.PI * i / vertexCount;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            graph.addNode(new GraphNode(String.valueOf(i), x, y));
        }

        if (caseType.equals("Sparse Graph")) {
            for (int i = 0; i < vertexCount - 1; i++) {
                graph.addEdge(new GraphEdge(String.valueOf(i), String.valueOf(i + 1), (i % 5) + 1));
            }
        } else { // Dense Graph
            for (int i = 0; i < vertexCount; i++) {
                for (int j = i + 1; j < vertexCount; j++) {
                    graph.addEdge(new GraphEdge(String.valueOf(i), String.valueOf(j), ((i + j) % 5) + 1));
                }
            }
        }
        return graph;
    }
}