package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RaceAlgorithmSelectController implements Initializable {

    @FXML private ComboBox<String> algorithm1ComboBox;
    @FXML private ComboBox<String> algorithm2ComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String category = RaceSession.getInstance().getCategory();

        if (category.equals("Sorting")) {
            algorithm1ComboBox.setItems(FXCollections.observableArrayList("Insertion Sort", "Quick Sort"));
            algorithm2ComboBox.setItems(FXCollections.observableArrayList("Insertion Sort", "Quick Sort"));
        } else {
            algorithm1ComboBox.setItems(FXCollections.observableArrayList("Dijkstra", "Bellman-Ford"));
            algorithm2ComboBox.setItems(FXCollections.observableArrayList("Dijkstra", "Bellman-Ford"));
        }
    }

    @FXML
    private void handleContinue() {
        String name1 = algorithm1ComboBox.getValue();
        String name2 = algorithm2ComboBox.getValue();

        if (name1 == null || name2 == null) {
            new Alert(Alert.AlertType.ERROR, "Pick both algorithms first").showAndWait();
            return;
        }

        RaceSession session = RaceSession.getInstance();
        session.setAlgorithm1Name(name1);
        session.setAlgorithm2Name(name2);

        SceneManager.switchTo("RaceInputSetup.fxml");
    }
}