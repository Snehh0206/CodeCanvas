package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.model.AlgorithmStep;
import com.codecanvas.model.RaceSession;
import com.codecanvas.service.SceneManager;
import com.codecanvas.visualization.BarVisualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RaceModeController implements Initializable {

    @FXML private Label algorithm1NameLabel;
    @FXML private Label algorithm2NameLabel;
    @FXML private Pane visualizationPane1;
    @FXML private Pane visualizationPane2;
    @FXML private Label stats1Label;
    @FXML private Label stats2Label;
    @FXML private Label timeComplexity1Label;
    @FXML private Label timeComplexity2Label;
    @FXML private Label executionTime1Label;
    @FXML private Label executionTime2Label;
    @FXML private ProgressBar progress1Bar;
    @FXML private ProgressBar progress2Bar;
    @FXML private Label resultLabel;

    private final BarVisualizer visualizer1 = new BarVisualizer();
    private final BarVisualizer visualizer2 = new BarVisualizer();

    private final int[] defaultInput = {8, 3, 9, 1, 6, 4, 7, 2};

    private long time1Micros;
    private long time2Micros;
    private boolean race1Done = false;
    private boolean race2Done = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RaceSession session = RaceSession.getInstance();

        String name1 = session.getAlgorithm1Name();
        String name2 = session.getAlgorithm2Name();

        int[] input1 = session.isCustom1() && session.getCustomValues1() != null
                ? session.getCustomValues1() : defaultInput;
        int[] input2 = session.isCustom2() && session.getCustomValues2() != null
                ? session.getCustomValues2() : defaultInput;

        Algorithm algo1 = createAlgorithm(name1);
        Algorithm algo2 = createAlgorithm(name2);

        if (algo1 == null || algo2 == null) {
            resultLabel.setText("Graph race coming soon — pick sorting algorithms for now");
            return;
        }

        algorithm1NameLabel.setText(algo1.getName());
        algorithm2NameLabel.setText(algo2.getName());

        long start1 = System.nanoTime();
        List<AlgorithmStep> steps1 = algo1.run(input1);
        time1Micros = (System.nanoTime() - start1) / 1_000;

        long start2 = System.nanoTime();
        List<AlgorithmStep> steps2 = algo2.run(input2);
        time2Micros = (System.nanoTime() - start2) / 1_000;

        String case1 = classifyCase(name1, input1);
        String case2 = classifyCase(name2, input2);
        timeComplexity1Label.setText("Complexity: " + describeComplexity(name1, case1) + " (" + case1 + ")");
        timeComplexity2Label.setText("Complexity: " + describeComplexity(name2, case2) + " (" + case2 + ")");

        visualizer1.initialize(visualizationPane1, input1);
        visualizer2.initialize(visualizationPane2, input2);

        resultLabel.setText("Racing...");

        playSteps(steps1, visualizer1, progress1Bar, stats1Label, executionTime1Label, true);
        playSteps(steps2, visualizer2, progress2Bar, stats2Label, executionTime2Label, false);
    }

    // Looks at the input's shape to decide which theoretical case actually applies
    private String classifyCase(String algorithmName, int[] input) {
        boolean ascending = isSorted(input, true);
        boolean descending = isSorted(input, false);

        if (algorithmName.equals("Insertion Sort")) {
            if (ascending) return "Best Case";
            if (descending) return "Worst Case";
            return "Average Case";
        } else { // Quick Sort (last-element pivot): sorted input in either direction is worst case
            if (ascending || descending) return "Worst Case";
            return "Average Case";
        }
    }

    private String describeComplexity(String algorithmName, String caseLabel) {
        if (algorithmName.equals("Insertion Sort")) {
            return switch (caseLabel) {
                case "Best Case" -> "O(n)";
                case "Worst Case" -> "O(n^2)";
                default -> "O(n^2)";
            };
        } else {
            return switch (caseLabel) {
                case "Worst Case" -> "O(n^2)";
                default -> "O(n log n)";
            };
        }
    }

    private boolean isSorted(int[] array, boolean ascending) {
        for (int i = 1; i < array.length; i++) {
            if (ascending && array[i] < array[i - 1]) return false;
            if (!ascending && array[i] > array[i - 1]) return false;
        }
        return true;
    }

    private void playSteps(List<AlgorithmStep> steps, BarVisualizer visualizer, ProgressBar progressBar,
                           Label statsLabel, Label timeLabel, boolean isFirst) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < steps.size(); i++) {
            AlgorithmStep step = steps.get(i);
            int stepIndex = i;

            KeyFrame frame = new KeyFrame(Duration.millis(150.0 * i), event -> {
                visualizer.animateToStep(step);
                progressBar.setProgress((stepIndex + 1) / (double) steps.size());
                statsLabel.setText("Comparisons: " + step.getComparisons() + " | Swaps: " + step.getSwaps());

                if (stepIndex == steps.size() - 1) {
                    long micros = isFirst ? time1Micros : time2Micros;
                    timeLabel.setText("Computation Time: " + micros + " Β΅s");
                    if (isFirst) race1Done = true; else race2Done = true;
                    checkRaceFinished();
                }
            });
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    private void checkRaceFinished() {
        if (race1Done && race2Done) {
            String name1 = algorithm1NameLabel.getText();
            String name2 = algorithm2NameLabel.getText();

            if (time1Micros < time2Micros) {
                resultLabel.setText(name1 + " wins! (" + time1Micros + "Β΅s vs " + time2Micros + "Β΅s)");
            } else if (time2Micros < time1Micros) {
                resultLabel.setText(name2 + " wins! (" + time2Micros + "Β΅s vs " + time1Micros + "Β΅s)");
            } else {
                resultLabel.setText("It's a tie! (" + time1Micros + "Β΅s each)");
            }
        }
    }

    private Algorithm createAlgorithm(String name) {
        return switch (name) {
            case "Insertion Sort" -> new InsertionSort();
            case "Quick Sort" -> new QuickSort();
            default -> null;
        };
    }

    @FXML
    private void handleBack() {
        SceneManager.goBack();
    }

    @FXML
    private void handleDashboard() {
        SceneManager.goToDashboard();
    }
}