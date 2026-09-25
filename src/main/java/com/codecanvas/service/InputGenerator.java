package com.codecanvas.service;

import java.util.Random;

public class InputGenerator {

    private static final Random random = new Random();

    public static int[] generate(String algorithmName, String caseType, int size) {
        int[] array = new int[size];

        boolean wantAscending = algorithmName.equals("Insertion Sort") && caseType.equals("Best Case");
        boolean wantDescending = algorithmName.equals("Insertion Sort") && caseType.equals("Worst Case");
        boolean wantSortedForQuickSort = algorithmName.equals("Quick Sort") && caseType.equals("Worst Case");

        if (wantAscending || wantSortedForQuickSort) {
            for (int i = 0; i < size; i++) array[i] = i + 1;
            return array;
        }

        if (wantDescending) {
            for (int i = 0; i < size; i++) array[i] = size - i;
            return array;
        }

        for (int i = 0; i < size; i++) array[i] = i + 1;
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return array;
    }
}