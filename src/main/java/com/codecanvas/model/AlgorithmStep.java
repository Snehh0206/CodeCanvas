package com.codecanvas.model;

import java.util.List;

public class AlgorithmStep {
    private final int[] arrayState;
    private final List<Integer> comparingIndices;
    private final List<Integer> swappingIndices;
    private final List<Integer> sortedIndices;
    private final int comparisons;
    private final int swaps;
    private final String description;

    public AlgorithmStep(int[] arrayState, List<Integer> comparingIndices,
                         List<Integer> swappingIndices, List<Integer> sortedIndices,
                         int comparisons, int swaps, String description) {
        this.arrayState = arrayState.clone();
        this.comparingIndices = comparingIndices;
        this.swappingIndices = swappingIndices;
        this.sortedIndices = sortedIndices;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.description = description;
    }

    public int[] getArrayState() { return arrayState; }
    public List<Integer> getComparingIndices() { return comparingIndices; }
    public List<Integer> getSwappingIndices() { return swappingIndices; }
    public List<Integer> getSortedIndices() { return sortedIndices; }
    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }
    public String getDescription() { return description; }
}