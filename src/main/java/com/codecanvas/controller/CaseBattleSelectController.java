package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CaseBattleSelectController implements Initializable {

    @FXML private ComboBox<String> algorithmAComboBox;
    @FXML private ComboBox<String> caseAComboBox;
    @FXML private ComboBox<String> algorithmBComboBox;
    @FXML private ComboBox<String> caseBComboBox;
    @FXML private TextField inputSizeField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmAComboBox.setItems(FXCollections.observableArrayList("Insertion Sort", "Quick Sort"));
        algorithmBComboBox.setItems(FXCollections.observableArrayList("Insertion Sort", "Quick Sort"));
        caseAComboBox.setItems(FXCollections.observableArrayList("Best Case", "Average Case", "Worst Case"));
        caseBComboBox.setItems(FXCollections.observableArrayList("Best Case", "Average Case", "Worst Case"));
    }

    @FXML
    private void handleStartBattle() {
        String algoA = algorithmAComboBox.getValue();
        String caseA = caseAComboBox.getValue();
        String algoB = algorithmBComboBox.getValue();
        String caseB = caseBComboBox.getValue();

        if (algoA == null || caseA == null || algoB == null || caseB == null) {
            new Alert(Alert.AlertType.ERROR, "Pick both algorithms and both cases").showAndWait();
            return;
        }

        int size;
        try {
            size = Integer.parseInt(inputSizeField.getText().trim());
            if (size < 2 || size > 50) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter a size between 2 and 50").showAndWait();
            return;
        }

        RaceSession session = RaceSession.getInstance();
        session.setAlgorithm1Name(algoA);
        session.setAlgorithm2Name(algoB);
        session.setCaseBattle(true);
        session.setCase1(caseA);
        session.setCase2(caseB);
        session.setBattleInputSize(size);

        SceneManager.switchTo("RaceMode.fxml");
    }

    @FXML
    private void handleBack() {
        SceneManager.goBack();
    }

    @FXML
    private void handleDashboard() {
        SceneManager.goToDashboard();
    }
}