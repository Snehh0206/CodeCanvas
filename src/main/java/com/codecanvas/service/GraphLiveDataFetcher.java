package com.codecanvas.service;

import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GraphLiveDataFetcher {

    // Uses live exchange rates (USD base) as edge weights between 5 currency "cities" —
    // a real, live, changing dataset feeding directly into Dijkstra/Bellman-Ford.
    public static Graph fetchLiveGraph() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.exchangerate-api.com/v4/latest/USD"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode rates = root.get("rates");

        String[] currencies = {"USD", "EUR", "GBP", "JPY", "INR"};
        Graph graph = new Graph();
        double centerX = 200, centerY = 150, radius = 120;

        for (int i = 0; i < currencies.length; i++) {
            double angle = 2 * Math.PI * i / currencies.length;
            graph.addNode(new GraphNode(String.valueOf(i),
                    centerX + radius * Math.cos(angle), centerY + radius * Math.sin(angle)));
        }

        for (int i = 0; i < currencies.length; i++) {
            for (int j = i + 1; j < currencies.length; j++) {
                double rateI = currencies[i].equals("USD") ? 1.0 : rates.get(currencies[i]).asDouble();
                double rateJ = currencies[j].equals("USD") ? 1.0 : rates.get(currencies[j]).asDouble();
                int weight = (int) Math.max(1, Math.round(Math.abs(rateI - rateJ) * 10));
                graph.addEdge(new GraphEdge(String.valueOf(i), String.valueOf(j), weight));
            }
        }
        return graph;
    }
}