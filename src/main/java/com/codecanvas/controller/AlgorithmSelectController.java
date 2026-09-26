package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.SceneManager;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class AlgorithmSelectController {
    @FXML
    private void handleBack() { SceneManager.goBack(); }

    @FXML
    private void handleDashboard() { SceneManager.goToDashboard(); }
    @FXML
    private void handleInsertionSort() { selectAndProceed("Insertion Sort"); }

    @FXML
    private void handleQuickSort() { selectAndProceed("Quick Sort"); }

    @FXML
    private void handleDijkstra() { selectAndProceed("Dijkstra"); }

    @FXML
    private void handleBellmanFord() { selectAndProceed("Bellman-Ford"); }

    private void selectAndProceed(String algorithmName) {
        AlgorithmSession.getInstance().setSelectedAlgorithm(algorithmName);
        SceneManager.switchTo("InputSetup.fxml");
    }
}