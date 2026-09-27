package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class AlgorithmSelectController extends BaseController {

    @FXML private void handleInsertionSort() { selectAndProceed("Insertion Sort"); }
    @FXML private void handleQuickSort() { selectAndProceed("Quick Sort"); }
    @FXML private void handleMergeSort() { selectAndProceed("Merge Sort"); }
    @FXML private void handleDijkstra() { selectAndProceed("Dijkstra"); }
    @FXML private void handleBellmanFord() { selectAndProceed("Bellman-Ford"); }
    @FXML private void handlePrims() { selectAndProceed("Prim's Algorithm"); }
    @FXML private void handleKruskal() { selectAndProceed("Kruskal's Algorithm"); }
    @FXML private void handleFloydWarshall() { selectAndProceed("Floyd-Warshall"); }
    @FXML private void handleBFS() { selectAndProceed("BFS"); }
    @FXML private void handleDFS() { selectAndProceed("DFS"); }

    private void selectAndProceed(String algorithmName) {
        AlgorithmSession.getInstance().setSelectedAlgorithm(algorithmName);
        boolean isGraph = algorithmName.equals("Dijkstra") || algorithmName.equals("Bellman-Ford")
                || algorithmName.equals("Prim's Algorithm") || algorithmName.equals("Kruskal's Algorithm")
                || algorithmName.equals("Floyd-Warshall") || algorithmName.equals("BFS") || algorithmName.equals("DFS");
        SceneManager.switchTo(isGraph ? "GraphInputSetup.fxml" : "SortingInputSetup.fxml");
    }
}