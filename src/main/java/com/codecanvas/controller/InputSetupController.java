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
    @FXML private TextField vertexCountField;
    @FXML private TextField edgesField;

    private boolean usingCustomInput = false;


    @FXML
    private void handleBack() { SceneManager.goBack(); }

    @FXML
    private void handleDashboard() { SceneManager.goToDashboard(); }
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
        String selected = session.getSelectedAlgorithm();
        boolean isGraph = selected.equals("Dijkstra") || selected.equals("Bellman-Ford");

        session.setQuizMode(quizModeCheckBox.isSelected());
        if (isGraph) {
            try {
                int vertexCount = Integer.parseInt(vertexCountField.getText().trim());
                String edgesText = edgesField.getText().trim();
                if (edgesText.isEmpty() || vertexCount < 2) throw new NumberFormatException();

                com.codecanvas.service.GraphInputParser.parse(vertexCount, edgesText); // validate

                session.setCustomGraph(true);
                session.setVertexCount(vertexCount);
                session.setEdgesText(edgesText);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,
                        "Enter vertex count (2+) and edges like: 0-1,0-2,2-3").showAndWait();
                return;
            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                return;
            }
        }
        else {
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
                    new Alert(Alert.AlertType.ERROR, "Enter numbers separated by commas, e.g. 5,2,9,1").showAndWait();
                    return;
                }
            }
        }

        SceneManager.switchTo("AlgorithmLab.fxml");
    }
}