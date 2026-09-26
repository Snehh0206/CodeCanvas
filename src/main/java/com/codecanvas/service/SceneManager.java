package com.codecanvas.service;

import com.codecanvas.model.AppSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneManager {

    private static Stage primaryStage;
    private static final Deque<String> history = new ArrayDeque<>();
    private static String currentFxml;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFileName) {
        if (currentFxml != null) {
            history.push(currentFxml);
        }
        load(fxmlFileName);
    }

    public static void goBack() {
        if (!history.isEmpty()) {
            String previous = history.pop();
            load(previous);
        }
    }

    public static void goToDashboard() {
        history.clear();
        load("Dashboard.fxml");
    }

    // Reapplies the current theme without navigating — called after toggling in Settings
    public static void refreshTheme() {
        if (primaryStage != null && primaryStage.getScene() != null) {
            applyTheme(primaryStage.getScene());
        }
    }

    private static void load(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/com/codecanvas/fxml/" + fxmlFileName)
            );
            Parent root = loader.load();
            Scene scene = primaryStage.getScene();

            if (scene == null) {
                scene = new Scene(root, 900, 600);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            applyTheme(scene);
            currentFxml = fxmlFileName;
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String cssFile = AppSettings.getInstance().isDarkMode() ? "dark-theme.css" : "light-theme.css";
        scene.getStylesheets().add(SceneManager.class.getResource("/com/codecanvas/css/" + cssFile).toExternalForm());
    }
}