package com.codecanvas.model;

public class AlgorithmSession {

    private static final AlgorithmSession instance = new AlgorithmSession();

    private String selectedAlgorithm;
    private boolean customInput;
    private int[] customValues;
    private boolean quizMode;

    private AlgorithmSession() {}

    public static AlgorithmSession getInstance() {
        return instance;
    }

    public String getSelectedAlgorithm() { return selectedAlgorithm; }
    public void setSelectedAlgorithm(String algorithm) { this.selectedAlgorithm = algorithm; }

    public boolean isCustomInput() { return customInput; }
    public void setCustomInput(boolean customInput) { this.customInput = customInput; }

    public int[] getCustomValues() { return customValues; }
    public void setCustomValues(int[] customValues) { this.customValues = customValues; }

    public boolean isQuizMode() { return quizMode; }
    public void setQuizMode(boolean quizMode) { this.quizMode = quizMode; }
}