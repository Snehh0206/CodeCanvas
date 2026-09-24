package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RaceInputSetupController {

    @FXML private TextField custom1Field;
    @FXML private TextField custom2Field;

    private boolean using1Custom = false;
    private boolean using2Custom = false;

    @FXML private void handleExample1() { using1Custom = false; }
    @FXML private void handleCustom1Toggle() { using1Custom = true; }
    @FXML private void handleExample2() { using2Custom = false; }
    @FXML private void handleCustom2Toggle() { using2Custom = true; }

    @FXML
    private void handleStartRace() {
        RaceSession session = RaceSession.getInstance();
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