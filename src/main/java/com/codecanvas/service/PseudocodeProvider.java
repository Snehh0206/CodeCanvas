package com.codecanvas.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PseudocodeProvider {

    private static Map<String, List<String>> pseudocodeMap;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = PseudocodeProvider.class
                    .getResourceAsStream("/com/codecanvas/data/pseudocode.json");
            pseudocodeMap = mapper.readValue(inputStream, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            pseudocodeMap = Map.of();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getLines(String algorithmName) {
        return (List<String>) pseudocodeMap.getOrDefault(algorithmName, List.of());
    }
}