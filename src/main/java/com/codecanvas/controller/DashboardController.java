package com.codecanvas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.codecanvas.service.SceneManager;

public class DashboardController {

    @FXML
    private Label experimentCountLabel;
    @FXML
    private Label recentAlgorithmLabel;
    @FXML
    private Label quizScoreLabel;
    @FXML
    private Label savedRunsLabel;
    @FXML
    private Label recentActivityLabel;

    @FXML
    private void handleAlgorithmLab() {
        SceneManager.switchTo("AlgorithmSelect.fxml");
    }

    @FXML
    private void handleRaceMode() {
        SceneManager.switchTo("RaceCategorySelect.fxml");
    }
    @FXML
    public void initialize() {
        loadStats();
    }
    private final com.codecanvas.database.RunDAO runDAO = new com.codecanvas.database.RunDAO();
    private final com.codecanvas.database.QuizResultDAO quizResultDAO = new com.codecanvas.database.QuizResultDAO();
    @FXML
    private void handleQuiz() {
        System.out.println("Quiz not built yet");
    }

    @FXML
    private void handleHistory() {
        SceneManager.switchTo("History.fxml");
    }

    @FXML
    private void handleStatistics() {
        SceneManager.switchTo("Statistics.fxml");
    }
    @FXML
    private void handleMasteryMap() {
        SceneManager.switchTo("MasteryMap.fxml");
    }

    @FXML
    private void handleSettings() {
        SceneManager.switchTo("Settings.fxml");
    }
    private void loadStats() {
        com.codecanvas.service.AppExecutor.submit(() -> {
            int runCount = runDAO.countAllRunsForUser();
            String recentAlgo = runDAO.getMostRecentAlgorithm();
            int[] quizTotals = quizResultDAO.getOverallQuizScoreForUser();

            javafx.application.Platform.runLater(() -> {
                experimentCountLabel.setText("Experiments: " + runCount);
                recentAlgorithmLabel.setText("Recent Algorithm: " + recentAlgo);
                quizScoreLabel.setText("Quiz Score: " + quizTotals[0] + "/" + quizTotals[1]);
                recentActivityLabel.setText("Welcome, " + com.codecanvas.model.UserSession.getInstance().getUsername() + "!");
            });
        });
    }
    @FXML
    private void handleLogout() {
        com.codecanvas.model.UserSession.getInstance().setUserId(-1);
        SceneManager.switchTo("Login.fxml");
    }
}