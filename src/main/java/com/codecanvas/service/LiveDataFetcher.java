package com.codecanvas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LiveDataFetcher {

    // Fetches live Bitcoin price history (last several days) from a free public API
    // and returns it as an int array usable as sorting input.
    public static int[] fetchLiveNumbers() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coingecko.com/api/v3/coins/bitcoin/market_chart?vs_currency=usd&days=10&interval=daily"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode prices = root.get("prices");

        List<Integer> values = new ArrayList<>();
        for (JsonNode entry : prices) {
            double price = entry.get(1).asDouble();
            values.add((int) Math.round(price / 100)); // scale down to reasonable bar heights
        }

        return values.stream().mapToInt(Integer::intValue).toArray();
    }
}