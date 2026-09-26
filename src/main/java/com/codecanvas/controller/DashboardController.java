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
}