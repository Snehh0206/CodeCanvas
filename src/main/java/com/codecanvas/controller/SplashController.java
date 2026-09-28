package com.codecanvas.controller;

import com.codecanvas.service.SceneManager;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML private Pane floatingLayer;
    @FXML private VBox contentBox;

    private static final String[] COLORS = {"#fadcd5", "#c98a9b", "#765d67", "#6d3c52"};
    private final List<Animation> animations = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < 16; i++) {
            double fx = random.nextDouble();
            double fy = 0.1 + random.nextDouble() * 0.8;
            Color color = Color.web(COLORS[random.nextInt(COLORS.length)], 0.45);

            if (i % 4 == 0) addBlock(fx, fy, color);
            else addNode(fx, fy, color);
        }
        playIntro();
    }

    // A graph-node style circle, positioned as a fraction of the window size
    private void addNode(double fx, double fy, Color color) {
        Circle circle = new Circle(8 + random.nextInt(14), color);
        circle.centerXProperty().bind(floatingLayer.widthProperty().multiply(fx));
        circle.centerYProperty().bind(floatingLayer.heightProperty().multiply(fy));
        floatingLayer.getChildren().add(circle);
        drift(circle);
    }

    // An array-bar style block
    private void addBlock(double fx, double fy, Color color) {
        Rectangle block = new Rectangle(26, 26 + random.nextInt(30), color);
        block.setArcWidth(8);
        block.setArcHeight(8);
        block.xProperty().bind(floatingLayer.widthProperty().multiply(fx));
        block.yProperty().bind(floatingLayer.heightProperty().multiply(fy));
        floatingLayer.getChildren().add(block);
        drift(block);
    }

    private void drift(Node shape) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(3 + random.nextDouble() * 4), shape);
        move.setByY(-25 - random.nextInt(35));
        move.setAutoReverse(true);
        move.setCycleCount(Animation.INDEFINITE);
        move.play();
        animations.add(move);
    }

    private void playIntro() {
        contentBox.setOpacity(0);
        contentBox.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.millis(1100), contentBox);
        fade.setToValue(1);
        TranslateTransition rise = new TranslateTransition(Duration.millis(1100), contentBox);
        rise.setToY(0);

        fade.play();
        rise.play();
    }

    @FXML
    private void handleGetStarted() {
        animations.forEach(Animation::stop);
        SceneManager.switchTo("Login.fxml");
    }
}