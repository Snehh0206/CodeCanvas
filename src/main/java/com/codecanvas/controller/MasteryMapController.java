package com.codecanvas.controller;

import com.codecanvas.database.QuizResultDAO;
import com.codecanvas.database.RunDAO;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.SceneManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class MasteryMapController implements Initializable {

    @FXML private Label insertionSortLabel;
    @FXML private ProgressBar insertionSortBar;
    @FXML private Label quickSortLabel;
    @FXML private ProgressBar quickSortBar;
    @FXML private Label dijkstraLabel;
    @FXML private ProgressBar dijkstraBar;
    @FXML private Label bellmanFordLabel;
    @FXML private ProgressBar bellmanFordBar;

    private final RunDAO runDAO = new RunDAO();
    private final QuizResultDAO quizResultDAO = new QuizResultDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMastery("Insertion Sort", insertionSortLabel, insertionSortBar);
        loadMastery("Quick Sort", quickSortLabel, quickSortBar);
        loadMastery("Dijkstra", dijkstraLabel, dijkstraBar);
        loadMastery("Bellman-Ford", bellmanFordLabel, bellmanFordBar);
    }

    private void loadMastery(String algorithm, Label label, ProgressBar bar) {
        AppExecutor.submit(() -> {
            int runCount = runDAO.countRunsForAlgorithm(algorithm);
            int[] quizTotals = quizResultDAO.getQuizTotalsForAlgorithm(algorithm);
            int correct = quizTotals[0];
            int total = quizTotals[1];

            double quizAccuracy = total > 0 ? (correct / (double) total) : 0;
            double runProgress = Math.min(runCount / 5.0, 1.0);
            double mastery = (quizAccuracy * 0.6) + (runProgress * 0.4);

            Platform.runLater(() -> {
                bar.setProgress(mastery);
                int percent = (int) (mastery * 100);
                label.setText(algorithm + ": " + percent + "% mastered ("
                        + correct + "/" + total + " quiz correct, " + runCount + " runs completed)");
            });
        });
    }

    @FXML
    private void handleBack() { SceneManager.goBack(); }

    @FXML
    private void handleDashboard() { SceneManager.goToDashboard(); }
}