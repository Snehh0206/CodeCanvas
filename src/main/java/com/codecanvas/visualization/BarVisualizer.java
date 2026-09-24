package com.codecanvas.visualization;

import com.codecanvas.model.AlgorithmStep;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.codecanvas.service.SoundManager;

public class BarVisualizer {

    private static final double PANE_HEIGHT = 300;
    private static final double PANE_WIDTH = 400;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);

    private static final Color COLOR_NEUTRAL = Color.web("#6c8ebf");
    private static final Color COLOR_COMPARING = Color.web("#f4d03f");
    private static final Color COLOR_SWAPPING = Color.web("#e74c3c");
    private static final Color COLOR_SORTED = Color.web("#2ecc71");

    private Rectangle[] bars;
    private Label[] valueLabels;
    private int max;

    // Call once per new run — builds the bars fresh
    public void initialize(Pane pane, int[] initialArray) {
        pane.getChildren().clear();
        int n = initialArray.length;
        bars = new Rectangle[n];
        valueLabels = new Label[n];

        max = 1;
        for (int value : initialArray) {
            if (value > max) max = value;
        }

        double barWidth = PANE_WIDTH / n - 4;

        for (int i = 0; i < n; i++) {
            double barHeight = (initialArray[i] / (double) max) * PANE_HEIGHT;
            double x = i * (PANE_WIDTH / n);
            double y = PANE_HEIGHT - barHeight;

            Rectangle bar = new Rectangle(x, y, barWidth, barHeight);
            bar.setFill(COLOR_NEUTRAL);

            Label label = new Label(String.valueOf(initialArray[i]));
            label.setLayoutX(x);
            label.setLayoutY(y - 18);

            bars[i] = bar;
            valueLabels[i] = label;
            pane.getChildren().addAll(bar, label);
        }
    }

    // Call on every Next/Previous/Restart — animates existing bars to the new state
    public void animateToStep(AlgorithmStep step) {
        int[] array = step.getArrayState();

        for (int i = 0; i < array.length; i++) {
            Rectangle bar = bars[i];
            double targetHeight = (array[i] / (double) max) * PANE_HEIGHT;
            double targetY = PANE_HEIGHT - targetHeight;

            Timeline sizeTimeline = new Timeline(
                    new KeyFrame(ANIMATION_DURATION,
                            new KeyValue(bar.heightProperty(), targetHeight),
                            new KeyValue(bar.yProperty(), targetY))
            );
            sizeTimeline.play();

            Color targetColor = colorFor(i, step);
            new FillTransition(ANIMATION_DURATION, bar, (Color) bar.getFill(), targetColor).play();

            valueLabels[i].setText(String.valueOf(array[i]));
            valueLabels[i].setLayoutY(targetY - 18);
        }

        if (!step.getSwappingIndices().isEmpty()) {
            SoundManager.playSwap();
        } else if (!step.getComparingIndices().isEmpty()) {
            SoundManager.playCompare();
        }
    }

    private Color colorFor(int index, AlgorithmStep step) {
        if (step.getSwappingIndices().contains(index)) return COLOR_SWAPPING;
        if (step.getComparingIndices().contains(index)) return COLOR_COMPARING;
        if (step.getSortedIndices().contains(index)) return COLOR_SORTED;
        return COLOR_NEUTRAL;
    }
}