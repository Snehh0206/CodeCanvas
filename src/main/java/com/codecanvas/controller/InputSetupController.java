package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class InputSetupController {

    @FXML private Button exampleInputButton;
    @FXML private Button customInputButton;
    @FXML private TextField customInputField;
    @FXML private CheckBox quizModeCheckBox;

    private boolean usingCustomInput = false;

    @FXML
    private void handleExampleInput() {
        usingCustomInput = false;
        customInputField.setDisable(true);
    }

    @FXML
    private void handleCustomInputToggle() {
        usingCustomInput = true;
        customInputField.setDisable(false);
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
                for (int i = 0; i < parts.length; i++) {
                    values[i] = Integer.parseInt(parts[i].trim());
                }
                session.setCustomValues(values);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter numbers separated by commas, e.g. 5,2,9,1");
                alert.showAndWait();
                return;
            }
        }

        SceneManager.switchTo("AlgorithmLab.fxml");
    }
}