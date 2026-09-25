package com.codecanvas;

import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import com.codecanvas.database.DatabaseHelper;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.initializeDatabase();
        primaryStage.setTitle("CodeCanvas");
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchTo("Splash.fxml");
    }

    @Override
    public void stop() {
        AppExecutor.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}