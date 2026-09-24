package com.codecanvas.service;

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

    // Normal navigation — remembers where you came from
    public static void switchTo(String fxmlFileName) {
        if (currentFxml != null) {
            history.push(currentFxml);
        }
        load(fxmlFileName);
    }

    // Goes back one screen — does not add to history (avoids forward-back loops)
    public static void goBack() {
        if (!history.isEmpty()) {
            String previous = history.pop();
            load(previous);
        }
    }

    // Jumps straight to Dashboard and clears history (fresh start point)
    public static void goToDashboard() {
        history.clear();
        load("Dashboard.fxml");
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

            currentFxml = fxmlFileName;
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}