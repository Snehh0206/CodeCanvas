package com.codecanvas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.codecanvas.service.SceneManager;

public class SplashController {

    @FXML
    private Label titleLabel;

    @FXML
    private Button getStartedButton;

    @FXML
    private void handleGetStarted() {
        SceneManager.switchTo("Dashboard.fxml");
    }
}