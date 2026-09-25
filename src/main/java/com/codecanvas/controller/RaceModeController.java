package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.algorithm.graph.BellmanFord;
import com.codecanvas.algorithm.graph.Dijkstra;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.model.AlgorithmStep;
import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;
import com.codecanvas.model.GraphStep;
import com.codecanvas.model.RaceSession;
import com.codecanvas.visualization.BarVisualizer;
import com.codecanvas.visualization.GraphVisualizer;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.GraphInputParser;

import com.codecanvas.service.SceneManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.application.Platform;

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

    private final BarVisualizer barVisualizer1 = new BarVisualizer();
    private final BarVisualizer barVisualizer2 = new BarVisualizer();
    private final GraphVisualizer graphVisualizer1 = new GraphVisualizer();
    private final GraphVisualizer graphVisualizer2 = new GraphVisualizer();

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
        boolean isGraph = isGraphAlgorithm(name1);

        algorithm1NameLabel.setText(name1);
        algorithm2NameLabel.setText(name2);
        resultLabel.setText("Racing...");

        if (isGraph) {
            runGraphRace(name1, name2);
        } else {
            runSortingRace(session, name1, name2);
        }
    }

    private boolean isGraphAlgorithm(String name) {
        return name.equals("Dijkstra") || name.equals("Bellman-Ford");
    }

    // ---------- SORTING RACE ----------

    private void runSortingRace(RaceSession session, String name1, String name2) {
        int[] input1 = session.isCustom1() && session.getCustomValues1() != null
                ? session.getCustomValues1() : defaultInput;
        int[] input2 = session.isCustom2() && session.getCustomValues2() != null
                ? session.getCustomValues2() : defaultInput;

        Algorithm algo1 = createSortingAlgorithm(name1);
        Algorithm algo2 = createSortingAlgorithm(name2);

        String case1 = classifyCase(name1, input1);
        String case2 = classifyCase(name2, input2);
        timeComplexity1Label.setText("Complexity: " + describeComplexity(name1, case1) + " (" + case1 + ")");
        timeComplexity2Label.setText("Complexity: " + describeComplexity(name2, case2) + " (" + case2 + ")");

        barVisualizer1.initialize(visualizationPane1, input1);
        barVisualizer2.initialize(visualizationPane2, input2);

        AppExecutor.submit(() -> {
            long start = System.nanoTime();
            List<AlgorithmStep> steps = algo1.run(input1);
            time1Micros = (System.nanoTime() - start) / 1_000;

            Platform.runLater(() ->
                    playSortingSteps(steps, barVisualizer1, progress1Bar, stats1Label, executionTime1Label, true));
        });

        AppExecutor.submit(() -> {
            long start = System.nanoTime();
            List<AlgorithmStep> steps = algo2.run(input2);
            time2Micros = (System.nanoTime() - start) / 1_000;

            Platform.runLater(() ->
                    playSortingSteps(steps, barVisualizer2, progress2Bar, stats2Label, executionTime2Label, false));
        });
    }

    private void playSortingSteps(List<AlgorithmStep> steps, BarVisualizer visualizer, ProgressBar progressBar,
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

    // ---------- GRAPH RACE ----------

    private void runGraphRace(String name1, String name2) {
        RaceSession session = RaceSession.getInstance();

        Graph graph1;
        Graph graph2;
        String startNode;

        if (session.isCustomGraph()) {
            graph1 = GraphInputParser.parse(session.getRaceVertexCount(), session.getEdges1Text());
            graph2 = GraphInputParser.parse(session.getRaceVertexCount(), session.getEdges2Text());
            startNode = "0";
        } else {
            graph1 = buildExampleGraph();
            graph2 = buildExampleGraph();
            startNode = "A";
        }

        GraphAlgorithm algo1 = createGraphAlgorithm(name1);
        GraphAlgorithm algo2 = createGraphAlgorithm(name2);

        if (algo1 == null || algo2 == null) {
            resultLabel.setText("Error: unrecognized algorithm");
            return;
        }

        timeComplexity1Label.setText("Complexity: " + algo1.getTheoreticalComplexity());
        timeComplexity2Label.setText("Complexity: " + algo2.getTheoreticalComplexity());

        graphVisualizer1.initialize(visualizationPane1, graph1);
        graphVisualizer2.initialize(visualizationPane2, graph2);

        Graph finalGraph1 = graph1;
        Graph finalGraph2 = graph2;

        AppExecutor.submit(() -> {
            long start = System.nanoTime();
            List<GraphStep> steps = algo1.run(finalGraph1, startNode);
            time1Micros = (System.nanoTime() - start) / 1_000;

            Platform.runLater(() ->
                    playGraphSteps(steps, graphVisualizer1, progress1Bar, stats1Label, executionTime1Label, true));
        });

        AppExecutor.submit(() -> {
            long start = System.nanoTime();
            List<GraphStep> steps = algo2.run(finalGraph2, startNode);
            time2Micros = (System.nanoTime() - start) / 1_000;

            Platform.runLater(() ->
                    playGraphSteps(steps, graphVisualizer2, progress2Bar, stats2Label, executionTime2Label, false));
        });
    }

    private void playGraphSteps(List<GraphStep> steps, GraphVisualizer visualizer, ProgressBar progressBar,
                                Label statsLabel, Label timeLabel, boolean isFirst) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < steps.size(); i++) {
            GraphStep step = steps.get(i);
            int stepIndex = i;

            KeyFrame frame = new KeyFrame(Duration.millis(150.0 * i), event -> {
                visualizer.animateToStep(step);
                progressBar.setProgress((stepIndex + 1) / (double) steps.size());
                statsLabel.setText("Comparisons: " + step.getComparisons() + " | Relaxations: " + step.getRelaxations());

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

    private Graph buildExampleGraph() {
        Graph graph = new Graph();
        graph.addNode(new GraphNode("A", 50, 50));
        graph.addNode(new GraphNode("B", 200, 50));
        graph.addNode(new GraphNode("C", 50, 200));
        graph.addNode(new GraphNode("D", 200, 200));

        graph.addEdge(new GraphEdge("A", "B", 4));
        graph.addEdge(new GraphEdge("A", "C", 1));
        graph.addEdge(new GraphEdge("C", "B", 2));
        graph.addEdge(new GraphEdge("B", "D", 5));
        graph.addEdge(new GraphEdge("C", "D", 8));
        return graph;
    }

    // ---------- SHARED ----------

    private void checkRaceFinished() {
        if (race1Done && race2Done) {
            String name1 = algorithm1NameLabel.getText();
            String name2 = algorithm2NameLabel.getText();

            if (time1Micros < time2Micros) {
                resultLabel.setText(name1 + " beat " + name2 + "! (" + time1Micros + "Β΅s vs " + time2Micros + "Β΅s)");
            } else if (time2Micros < time1Micros) {
                resultLabel.setText(name2 + " beat " + name1 + "! (" + time2Micros + "Β΅s vs " + time1Micros + "Β΅s)");
            } else {
                resultLabel.setText("It's a tie! (" + time1Micros + "Β΅s each)");
            }
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.goBack();
    }

    @FXML
    private void handleDashboard() {
        SceneManager.goToDashboard();
    }

    private String classifyCase(String algorithmName, int[] input) {
        boolean ascending = isSorted(input, true);
        boolean descending = isSorted(input, false);

        if (algorithmName.equals("Insertion Sort")) {
            if (ascending) return "Best Case";
            if (descending) return "Worst Case";
            return "Average Case";
        } else {
            if (ascending || descending) return "Worst Case";
            return "Average Case";
        }
    }

    private String describeComplexity(String algorithmName, String caseLabel) {
        if (algorithmName.equals("Insertion Sort")) {
            return switch (caseLabel) {
                case "Best Case" -> "O(n)";
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

    private Algorithm createSortingAlgorithm(String name) {
        return switch (name) {
            case "Insertion Sort" -> new InsertionSort();
            case "Quick Sort" -> new QuickSort();
            default -> null;
        };
    }

    private GraphAlgorithm createGraphAlgorithm(String name) {
        return switch (name) {
            case "Dijkstra" -> new Dijkstra();
            case "Bellman-Ford" -> new BellmanFord();
            default -> null;
        };
    }
}