package com.codecanvas.controller;

import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class GraphInputSetupController extends BaseController {

    @FXML private TextField vertexCountField;
    @FXML private TextField edgeCountField;
    @FXML private TextField edgesField;
    @FXML private CheckBox quizModeCheckBox;

    private boolean useCustomGraph = false;

    @FXML
    private void handleExampleGraph() {
        useCustomGraph = false;
        vertexCountField.setDisable(true);
        edgeCountField.setDisable(true);
        edgesField.setDisable(true);
    }

    @FXML
    private void handleCustomGraphToggle() {
        useCustomGraph = true;
        vertexCountField.setDisable(false);
        edgeCountField.setDisable(false);
        edgesField.setDisable(false);
    }

    @FXML
    private void handleContinue() {
        AlgorithmSession session = AlgorithmSession.getInstance();
        session.setQuizMode(quizModeCheckBox.isSelected());

        if (!useCustomGraph) {
            session.setCustomGraph(false);
            SceneManager.switchTo("AlgorithmLab.fxml");
            return;
        }

        try {
            int vertexCount = Integer.parseInt(vertexCountField.getText().trim());
            int edgeCount = Integer.parseInt(edgeCountField.getText().trim());
            String edgesText = edgesField.getText().trim();

            GraphInputParser.validateEdgeCount(vertexCount, edgeCount);

            int actualEdgeCount = edgesText.isEmpty() ? 0 : edgesText.split(",").length;
            if (actualEdgeCount != edgeCount) {
                new Alert(Alert.AlertType.ERROR,
                        "You said " + edgeCount + " edges but entered " + actualEdgeCount).showAndWait();
                return;
            }

            GraphInputParser.parse(vertexCount, edgesText);

            session.setCustomGraph(true);
            session.setVertexCount(vertexCount);
            session.setEdgesText(edgesText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter valid numbers for vertex/edge count").showAndWait();
            return;
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }

        SceneManager.switchTo("AlgorithmLab.fxml");
    }
}