package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class RaceCategorySelectController {

    @FXML
    private void handleSorting() {
        RaceSession.getInstance().setCategory("Sorting");
        SceneManager.switchTo("RaceAlgorithmSelect.fxml");
    }

    @FXML
    private void handleGraph() {
        RaceSession.getInstance().setCategory("Graph");
        SceneManager.switchTo("RaceAlgorithmSelect.fxml");
    }
}