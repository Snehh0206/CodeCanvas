package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class RaceInputSetupController {

    @FXML private TextField custom1Field;
    @FXML private TextField custom2Field;
    @FXML private TextField raceVertexCountField;
    @FXML private TextField raceEdges1Field;
    @FXML private TextField raceEdges2Field;

    private boolean using1Custom = false;
    private boolean using2Custom = false;

    @FXML
    private void handleExample1() {
        using1Custom = false;
        custom1Field.setDisable(true);
        custom1Field.clear();
    }

    @FXML
    private void handleCustom1Toggle() {
        using1Custom = true;
        custom1Field.setDisable(false);
    }

    @FXML
    private void handleExample2() {
        using2Custom = false;
        custom2Field.setDisable(true);
        custom2Field.clear();
    }

    @FXML
    private void handleCustom2Toggle() {
        using2Custom = true;
        custom2Field.setDisable(false);
    }

    @FXML
    private void handleStartRace() {
        RaceSession session = RaceSession.getInstance();
        String category = session.getCategory();

        if (category.equals("Graph")) {
            try {
                int vertexCount = Integer.parseInt(raceVertexCountField.getText().trim());
                String edges1 = raceEdges1Field.getText().trim();
                String edges2 = raceEdges2Field.getText().trim();
                if (edges1.isEmpty() || edges2.isEmpty() || vertexCount < 2) throw new NumberFormatException();

                // Validate both edge lists actually parse before leaving this screen
                com.codecanvas.service.GraphInputParser.parse(vertexCount, edges1);
                com.codecanvas.service.GraphInputParser.parse(vertexCount, edges2);

                session.setCustomGraph(true);
                session.setRaceVertexCount(vertexCount);
                session.setEdges1Text(edges1);
                session.setEdges2Text(edges2);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,
                        "Enter vertex count (2+) and edges for both, like: 0-1,0-2,2-3").showAndWait();
                return;
            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                return;
            }

            SceneManager.switchTo("RaceMode.fxml");
            return;
        }

        // existing sorting logic below, unchanged
        session.setCustom1(using1Custom);
        session.setCustom2(using2Custom);

        if (using1Custom) {
            int[] values = parseInput(custom1Field.getText());
            if (values == null) return;
            session.setCustomValues1(values);
        }

        if (using2Custom) {
            int[] values = parseInput(custom2Field.getText());
            if (values == null) return;
            session.setCustomValues2(values);
        }

        SceneManager.switchTo("RaceMode.fxml");
    }

    private int[] parseInput(String text) {
        try {
            String[] parts = text.split(",");
            int[] values = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                values[i] = Integer.parseInt(parts[i].trim());
            }
            return values;
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter numbers separated by commas, e.g. 5,2,9,1").showAndWait();
            return null;
        }
    }
}