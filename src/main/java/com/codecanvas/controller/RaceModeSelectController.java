package com.codecanvas.controller;

import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public class RaceModeSelectController {

    @FXML
    private void handleUserInput() {
        SceneManager.switchTo("RaceAlgorithmSelect.fxml");
    }

    @FXML
    private void handleCaseBattle() {
        String category = RaceSession.getInstance().getCategory();
        if (category.equals("Graph")) {
            SceneManager.switchTo("GraphCaseBattleSelect.fxml");
        } else {
            SceneManager.switchTo("CaseBattleSelect.fxml");
        }
    }

    @FXML
    private void handleBack() { SceneManager.goBack(); }

    @FXML
    private void handleDashboard() { SceneManager.goToDashboard(); }
}