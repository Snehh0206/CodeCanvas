package com.codecanvas.algorithm.sorting;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.model.AlgorithmStep;

import java.util.ArrayList;
import java.util.List;

public class InsertionSort implements Algorithm {

    @Override
    public List<AlgorithmStep> run(int[] input) {
        List<AlgorithmStep> steps = new ArrayList<>();
        int[] arr = input.clone();
        int comparisons = 0, swaps = 0;
        List<Integer> sorted = new ArrayList<>();
        sorted.add(0);

        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted),
                comparisons, swaps, "First element treated as sorted"));

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                steps.add(new AlgorithmStep(arr, List.of(j, j + 1), List.of(), new ArrayList<>(sorted),
                        comparisons, swaps, "Comparing " + arr[j] + " and " + key));

                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    swaps++;
                    steps.add(new AlgorithmStep(arr, List.of(), List.of(j, j + 1), new ArrayList<>(sorted),
                            comparisons, swaps, "Shifting " + arr[j] + " right"));
                    j--;
                } else break;
            }
            arr[j + 1] = key;
            sorted.add(i);
            steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted),
                    comparisons, swaps, "Placed " + key + " at position " + (j + 1)));
        }
        return steps;
    }

    @Override
    public String getName() { return "Insertion Sort"; }

    @Override
    public String getTheoreticalComplexity() { return "O(n^2) average/worst, O(n) best"; }
}