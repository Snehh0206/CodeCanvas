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

public class GraphCaseBattleSelectController implements Initializable {

    @FXML private ComboBox<String> algorithmAComboBox;
    @FXML private ComboBox<String> caseAComboBox;
    @FXML private ComboBox<String> algorithmBComboBox;
    @FXML private ComboBox<String> caseBComboBox;
    @FXML private TextField vertexCountField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmAComboBox.setItems(FXCollections.observableArrayList("Dijkstra", "Bellman-Ford"));
        algorithmBComboBox.setItems(FXCollections.observableArrayList("Dijkstra", "Bellman-Ford"));
        caseAComboBox.setItems(FXCollections.observableArrayList("Sparse Graph", "Dense Graph"));
        caseBComboBox.setItems(FXCollections.observableArrayList("Sparse Graph", "Dense Graph"));
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

        int vertexCount;
        try {
            vertexCount = Integer.parseInt(vertexCountField.getText().trim());
            if (vertexCount < 3 || vertexCount > 15) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter a vertex count between 3 and 15").showAndWait();
            return;
        }

        RaceSession session = RaceSession.getInstance();
        session.setAlgorithm1Name(algoA);
        session.setAlgorithm2Name(algoB);
        session.setCaseBattle(true);
        session.setCase1(caseA);
        session.setCase2(caseB);
        session.setBattleInputSize(vertexCount);

        SceneManager.switchTo("RaceMode.fxml");
    }

    @FXML
    private void handleBack() { SceneManager.goBack(); }

    @FXML
    private void handleDashboard() { SceneManager.goToDashboard(); }
}