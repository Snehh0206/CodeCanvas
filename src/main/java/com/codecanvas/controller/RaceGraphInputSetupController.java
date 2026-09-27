package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RaceGraphInputSetupController extends BaseController {

    @FXML private TextField vertexCountField;
    @FXML private TextField edgeCount1Field;
    @FXML private TextField edges1Field;
    @FXML private TextField edgeCount2Field;
    @FXML private TextField edges2Field;

    private boolean useCustomGraph = false;

    @FXML
    private void handleExampleGraph() {
        useCustomGraph = false;
        setFieldsDisabled(true);
    }

    @FXML
    private void handleCustomGraphToggle() {
        useCustomGraph = true;
        setFieldsDisabled(false);
    }

    private void setFieldsDisabled(boolean disabled) {
        vertexCountField.setDisable(disabled);
        edgeCount1Field.setDisable(disabled);
        edges1Field.setDisable(disabled);
        edgeCount2Field.setDisable(disabled);
        edges2Field.setDisable(disabled);
    }

    @FXML
    private void handleContinue() {
        RaceSession session = RaceSession.getInstance();

        if (!useCustomGraph) {
            session.setCustomGraph(false);
            SceneManager.switchTo("RaceMode.fxml");
            return;
        }

        try {
            int vertexCount = Integer.parseInt(vertexCountField.getText().trim());
            int edgeCount1 = Integer.parseInt(edgeCount1Field.getText().trim());
            int edgeCount2 = Integer.parseInt(edgeCount2Field.getText().trim());
            String edges1 = edges1Field.getText().trim();
            String edges2 = edges2Field.getText().trim();

            GraphInputParser.validateEdgeCount(vertexCount, edgeCount1);
            GraphInputParser.validateEdgeCount(vertexCount, edgeCount2);

            if (edges1.split(",").length != edgeCount1 || edges2.split(",").length != edgeCount2) {
                new Alert(Alert.AlertType.ERROR, "Edge count doesn't match the edges entered").showAndWait();
                return;
            }

            GraphInputParser.parse(vertexCount, edges1);
            GraphInputParser.parse(vertexCount, edges2);

            session.setCustomGraph(true);
            session.setRaceVertexCount(vertexCount);
            session.setEdges1Text(edges1);
            session.setEdges2Text(edges2);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter valid numbers for vertex/edge counts").showAndWait();
            return;
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }

        SceneManager.switchTo("RaceMode.fxml");
    }
}