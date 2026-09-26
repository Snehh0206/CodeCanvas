package com.codecanvas.controller;

import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;

public abstract class BaseController {

    @FXML
    protected void handleBack() {
        SceneManager.goBack();
    }

    @FXML
    protected void handleDashboard() {
        SceneManager.goToDashboard();
    }
}