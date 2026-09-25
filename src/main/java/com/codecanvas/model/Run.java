package com.codecanvas.model;

public class Run {
    private int id;
    private String algorithm;
    private String caseType;
    private int inputSize;
    private int steps;
    private int comparisons;
    private int swaps;
    private long executionTimeMicros;
    private String runDate;

    public Run(String algorithm, String caseType, int inputSize, int steps, int comparisons,
               int swaps, long executionTimeMicros, String runDate) {
        this.algorithm = algorithm;
        this.caseType = caseType;
        this.inputSize = inputSize;
        this.steps = steps;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.executionTimeMicros = executionTimeMicros;
        this.runDate = runDate;
    }

    public Run(int id, String algorithm, String caseType, int inputSize, int steps, int comparisons,
               int swaps, long executionTimeMicros, String runDate) {
        this(algorithm, caseType, inputSize, steps, comparisons, swaps, executionTimeMicros, runDate);
        this.id = id;
    }

    public int getId() { return id; }
    public String getAlgorithm() { return algorithm; }
    public String getCaseType() { return caseType; }
    public int getInputSize() { return inputSize; }
    public int getSteps() { return steps; }
    public int getComparisons() { return comparisons; }
    public int getSwaps() { return swaps; }
    public long getExecutionTimeMicros() { return executionTimeMicros; }
    public String getRunDate() { return runDate; }
}