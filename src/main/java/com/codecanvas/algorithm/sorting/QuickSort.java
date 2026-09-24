package com.codecanvas.algorithm.sorting;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.model.AlgorithmStep;

import java.util.ArrayList;
import java.util.List;

public class QuickSort implements Algorithm {

    private int comparisons;
    private int swaps;
    private List<AlgorithmStep> steps;
    private List<Integer> sorted;

    @Override
    public List<AlgorithmStep> run(int[] input) {
        steps = new ArrayList<>();
        sorted = new ArrayList<>();
        comparisons = 0;
        swaps = 0;
        int[] arr = input.clone();

        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted),
                comparisons, swaps, "Start"));
        quickSort(arr, 0, arr.length - 1);
        return steps;
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sorted.add(pivotIndex);
            steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted),
                    comparisons, swaps, "Pivot " + arr[pivotIndex] + " placed in final position"));
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        } else if (low == high) {
            sorted.add(low);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisons++;
            steps.add(new AlgorithmStep(arr, List.of(j, high), List.of(), new ArrayList<>(sorted),
                    comparisons, swaps, "Comparing " + arr[j] + " with pivot " + pivot));

            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
                steps.add(new AlgorithmStep(arr, List.of(), List.of(i, j), new ArrayList<>(sorted),
                        comparisons, swaps, "Swapped " + arr[i] + " and " + arr[j]));
            }
        }
        swap(arr, i + 1, high);
        steps.add(new AlgorithmStep(arr, List.of(), List.of(i + 1, high), new ArrayList<>(sorted),
                comparisons, swaps, "Placed pivot " + pivot));
        return i + 1;
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
        swaps++;
    }

    @Override
    public String getName() { return "Quick Sort"; }

    @Override
    public String getTheoreticalComplexity() { return "O(n log n) average, O(n^2) worst case"; }
}