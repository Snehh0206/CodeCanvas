package com.codecanvas;

import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import com.codecanvas.database.DatabaseHelper;
import com.codecanvas.database.AppSettingsDAO;
import com.codecanvas.model.AppSettings;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseHelper.initializeDatabase();
        primaryStage.setTitle("CodeCanvas");

        String[] saved = new AppSettingsDAO().getSettings();
        AppSettings.getInstance().setDarkMode(saved[0].equals("dark"));
        AppSettings.getInstance().setQuizDifficulty(saved[1]);
        AppSettings.getInstance().setDefaultSpeed(Integer.parseInt(saved[2]));

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