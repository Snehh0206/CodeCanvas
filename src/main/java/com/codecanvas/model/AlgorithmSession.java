package com.codecanvas.model;

public class AlgorithmSession {

    private static final AlgorithmSession instance = new AlgorithmSession();

    private String selectedAlgorithm;
    private boolean customInput;
    private int[] customValues;
    private boolean quizMode;
    private boolean customGraph;
    private int vertexCount;
    private String edgesText;

    public boolean isCustomGraph() { return customGraph; }
    public void setCustomGraph(boolean customGraph) { this.customGraph = customGraph; }

    public int getVertexCount() { return vertexCount; }
    public void setVertexCount(int vertexCount) { this.vertexCount = vertexCount; }

    public String getEdgesText() { return edgesText; }
    public void setEdgesText(String edgesText) { this.edgesText = edgesText; }
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