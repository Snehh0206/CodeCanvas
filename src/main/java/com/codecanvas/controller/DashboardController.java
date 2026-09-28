package com.codecanvas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.codecanvas.service.SceneManager;
import com.codecanvas.model.Run;
import com.codecanvas.model.UserSession;
import com.codecanvas.service.AppExecutor;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label experimentCountLabel;
    @FXML private Label recentAlgorithmLabel;
    @FXML private Label quizScoreLabel;
    @FXML private Label accuracyLabel;
    @FXML private VBox recentActivityBox;

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
        welcomeLabel.setText("Welcome back, " + UserSession.getInstance().getUsername() + "!");
        loadStats();
    }
    private final com.codecanvas.database.RunDAO runDAO = new com.codecanvas.database.RunDAO();
    private final com.codecanvas.database.QuizResultDAO quizResultDAO = new com.codecanvas.database.QuizResultDAO();
    @FXML
    private void handleQuiz() {
        System.out.println("QuizMode.fxml");
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
        AppExecutor.submit(() -> {
            int runCount = runDAO.countAllRunsForUser();
            String recentAlgo = runDAO.getMostRecentAlgorithm();
            int[] quiz = quizResultDAO.getOverallQuizScoreForUser();
            List<Run> recentRuns = runDAO.getRecentRuns(5);

            Platform.runLater(() -> {
                experimentCountLabel.setText(String.valueOf(runCount));
                recentAlgorithmLabel.setText(recentAlgo);
                quizScoreLabel.setText(quiz[0] + "/" + quiz[1]);
                int accuracy = quiz[1] > 0 ? (int) Math.round(quiz[0] * 100.0 / quiz[1]) : 0;
                accuracyLabel.setText(accuracy + "%");

                recentActivityBox.getChildren().clear();
                if (recentRuns.isEmpty()) {
                    recentActivityBox.getChildren().add(new Label("No activity yet. Start with Algorithm Lab."));
                } else {
                    for (Run run : recentRuns) {
                        Label row = new Label(run.getAlgorithm() + "  •  " + run.getInputSize()
                                + " items  •  " + run.getRunDate());
                        row.getStyleClass().add("activity-row");
                        recentActivityBox.getChildren().add(row);
                    }
                }
            });
        });
    }
    @FXML
    private void handleLogout() {
        com.codecanvas.model.UserSession.getInstance().setUserId(-1);
        SceneManager.switchTo("Login.fxml");
    }
}