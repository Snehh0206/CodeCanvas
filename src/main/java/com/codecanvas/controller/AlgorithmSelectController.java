package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class AlgorithmSelectController extends BaseController {

    @FXML private void handleInsertionSort() { selectAndProceed("Insertion Sort"); }
    @FXML private void handleQuickSort() { selectAndProceed("Quick Sort"); }
    @FXML private void handleDijkstra() { selectAndProceed("Dijkstra"); }
    @FXML private void handleBellmanFord() { selectAndProceed("Bellman-Ford"); }

    private void selectAndProceed(String algorithmName) {
        AlgorithmSession.getInstance().setSelectedAlgorithm(algorithmName);
        boolean isGraph = algorithmName.equals("Dijkstra") || algorithmName.equals("Bellman-Ford");
        SceneManager.switchTo(isGraph ? "GraphInputSetup.fxml" : "SortingInputSetup.fxml");
    }
}