package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class RaceCategorySelectController extends BaseController{

    @FXML
    private void handleSorting() {
        RaceSession.getInstance().setCategory("Sorting");
        SceneManager.switchTo("RaceModeSelect.fxml");
    }

    @FXML
    private void handleGraph() {
        RaceSession.getInstance().setCategory("Graph");
        SceneManager.switchTo("RaceModeSelect.fxml");
    }
}