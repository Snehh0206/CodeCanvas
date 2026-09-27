package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SortingInputSetupController extends BaseController {

    @FXML private TextField customInputField;
    @FXML private CheckBox quizModeCheckBox;

    private boolean usingCustomInput = false;

    @FXML
    private void handleExampleInput() {
        usingCustomInput = false;
        customInputField.setDisable(true);
        customInputField.clear();
    }

    @FXML
    private void handleCustomInput() {
        usingCustomInput = true;
        customInputField.setDisable(false);
    }
    @FXML
    private void handleFetchLiveData() {
        new Thread(() -> {
            try {
                int[] liveData = com.codecanvas.service.LiveDataFetcher.fetchLiveNumbers();
                javafx.application.Platform.runLater(() -> {
                    AlgorithmSession.getInstance().setCustomInput(true);
                    AlgorithmSession.getInstance().setCustomValues(liveData);
                    SceneManager.switchTo("AlgorithmLab.fxml");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR, "Couldn't fetch live data — check internet connection").showAndWait());
            }
        }).start();
    }
    @FXML
    private void handleContinue() {
        AlgorithmSession session = AlgorithmSession.getInstance();
        session.setQuizMode(quizModeCheckBox.isSelected());
        session.setCustomInput(usingCustomInput);

        if (usingCustomInput) {
            try {
                String[] parts = customInputField.getText().split(",");
                int[] values = new int[parts.length];
                for (int i = 0; i < parts.length; i++) values[i] = Integer.parseInt(parts[i].trim());
                session.setCustomValues(values);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Enter numbers separated by commas, e.g. 5,2,9,1").showAndWait();
                return;
            }
        }

        SceneManager.switchTo("AlgorithmLab.fxml");
    }
}