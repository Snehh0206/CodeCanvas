package com.codecanvas.service;

import com.codecanvas.model.QuizQuestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizBankFetcher {

    // Put your GitHub username here. The repo must be public for this URL to work.
    private static final String REMOTE_URL =
            "https://raw.githubusercontent.com/Snehh0206/CodeCanvas/main/quiz-questions.json";
    private static final String LOCAL_PATH = "/com/codecanvas/data/quiz-questions.json";

    public static class QuizBank {
        public final List<QuizQuestion> questions;
        public final String source;

        QuizBank(List<QuizQuestion> questions, String source) {
            this.questions = questions;
            this.source = source;
        }
    }

    public static QuizBank load(String algorithm) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        String source;

        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(REMOTE_URL))
                    .timeout(Duration.ofSeconds(8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("HTTP " + response.statusCode());
            }
            root = mapper.readTree(response.body());
            source = "Live from GitHub over HTTP (raw.githubusercontent.com)";
        } catch (Exception networkProblem) {
            try (InputStream in = QuizBankFetcher.class.getResourceAsStream(LOCAL_PATH)) {
                root = mapper.readTree(in);
            }
            source = "Offline copy (could not reach GitHub)";
        }

        List<QuizQuestion> questions = new ArrayList<>();
        for (JsonNode node : root) {
            if (!node.get("algorithm").asText().equals(algorithm)) continue;

            List<String> options = new ArrayList<>();
            for (JsonNode option : node.get("options")) options.add(option.asText());

            String correctText = options.get(node.get("answer").asInt());
            Collections.shuffle(options);

            questions.add(new QuizQuestion(node.get("question").asText(), options, options.indexOf(correctText)));
        }
        Collections.shuffle(questions);
        return new QuizBank(questions, source);
    }
}