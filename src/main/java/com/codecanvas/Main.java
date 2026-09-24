package com.codecanvas;

import com.codecanvas.service.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CodeCanvas");
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchTo("Splash.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}