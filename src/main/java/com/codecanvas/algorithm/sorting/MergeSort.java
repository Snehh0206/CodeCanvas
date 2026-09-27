package com.codecanvas.algorithm.sorting;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.model.AlgorithmStep;

import java.util.ArrayList;
import java.util.List;

public class MergeSort implements Algorithm {

    private List<AlgorithmStep> steps;
    private int comparisons;
    private int swaps;
    private List<Integer> sorted;

    @Override
    public List<AlgorithmStep> run(int[] input) {
        steps = new ArrayList<>();
        comparisons = 0;
        swaps = 0;
        sorted = new ArrayList<>();
        int[] arr = input.clone();

        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted), comparisons, swaps, "Start", 1));
        mergeSort(arr, 0, arr.length - 1);

        for (int i = 0; i < arr.length; i++) sorted.add(i);
        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted), comparisons, swaps, "Fully merged and sorted", 8));
        return steps;
    }

    private void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;

        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted), comparisons, swaps,
                "Splitting [" + left + ".." + right + "] at mid=" + mid, 2));

        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int[] leftArr = new int[mid - left + 1];
        int[] rightArr = new int[right - mid];
        System.arraycopy(arr, left, leftArr, 0, leftArr.length);
        System.arraycopy(arr, mid + 1, rightArr, 0, rightArr.length);

        int i = 0, j = 0, k = left;
        while (i < leftArr.length && j < rightArr.length) {
            comparisons++;
            steps.add(new AlgorithmStep(arr, List.of(left + i, mid + 1 + j), List.of(), new ArrayList<>(sorted),
                    comparisons, swaps, "Comparing " + leftArr[i] + " and " + rightArr[j], 5));

            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i]; i++;
            } else {
                arr[k] = rightArr[j]; j++;
            }
            swaps++;
            steps.add(new AlgorithmStep(arr, List.of(), List.of(k), new ArrayList<>(sorted),
                    comparisons, swaps, "Placed " + arr[k] + " at position " + k, 6));
            k++;
        }
        while (i < leftArr.length) { arr[k] = leftArr[i]; i++; k++; swaps++; }
        while (j < rightArr.length) { arr[k] = rightArr[j]; j++; k++; swaps++; }

        steps.add(new AlgorithmStep(arr, List.of(), List.of(), new ArrayList<>(sorted), comparisons, swaps,
                "Merged [" + left + ".." + right + "]", 7));
    }

    @Override
    public String getName() { return "Merge Sort"; }

    @Override
    public String getTheoreticalComplexity() { return "O(n log n) all cases"; }
}