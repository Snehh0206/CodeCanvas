package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.algorithm.graph.BellmanFord;
import com.codecanvas.algorithm.graph.Dijkstra;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.database.RunDAO;
import com.codecanvas.model.*;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.GraphCaseGenerator;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.InputGenerator;
import com.codecanvas.visualization.BarVisualizer;
import com.codecanvas.visualization.GraphVisualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RaceModeController extends BaseController implements Initializable {

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
    @FXML private Label thread1Label;
    @FXML private Label thread2Label;
    @FXML private ProgressBar progress1Bar;
    @FXML private ProgressBar progress2Bar;
    @FXML private Label resultLabel;
    @FXML private Label sourceInstructionLabel;
    @FXML private Button startRaceButton;
    @FXML private VBox distanceTracker1Box;
    @FXML private VBox distanceTracker2Box;

    private final BarVisualizer barVisualizer1 = new BarVisualizer();
    private final BarVisualizer barVisualizer2 = new BarVisualizer();
    private final GraphVisualizer graphVisualizer1 = new GraphVisualizer();
    private final GraphVisualizer graphVisualizer2 = new GraphVisualizer();
    private final RunDAO runDAO = new RunDAO();

    private final int[] defaultInput = {8, 3, 9, 1, 6, 4, 7, 2};

    private long time1Micros;
    private long time2Micros;
    private boolean race1Done = false;
    private boolean race2Done = false;

    private final Map<String, Label> tracker1Labels = new HashMap<>();
    private final Map<String, Label> tracker2Labels = new HashMap<>();

    private Graph pendingGraph1;
    private Graph pendingGraph2;
    private GraphAlgorithm pendingAlgo1;
    private GraphAlgorithm pendingAlgo2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        race1Done = false;
        race2Done = false;
        startRaceButton.setVisible(false);
        startRaceButton.setManaged(false);
        sourceInstructionLabel.setText("");
        RaceSession session = RaceSession.getInstance();

        if (session.isCaseBattle()) {
            runCaseBattle(session);
            return;
        }

        String name1 = session.getAlgorithm1Name();
        String name2 = session.getAlgorithm2Name();
        boolean isGraph = isGraphAlgorithm(name1);

        algorithm1NameLabel.setText(name1);
        algorithm2NameLabel.setText(name2);
        resultLabel.setText("Racing...");

        if (isGraph) {
            setupGraphSourceSelection(session, name1, name2);
        } else {
            runSortingRace(session, name1, name2);
        }
    }

    private boolean isGraphAlgorithm(String name) {
        return name.equals("Dijkstra") || name.equals("Bellman-Ford")
                || name.equals("Prim's Algorithm") || name.equals("Kruskal's Algorithm");
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
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<AlgorithmStep> steps = algo1.run(input1);
                time1Micros = (System.nanoTime() - start) / 1_000;

                Platform.runLater(() -> {
                    thread1Label.setText("Thread: " + threadName);
                    playSortingSteps(steps, barVisualizer1, progress1Bar, stats1Label, executionTime1Label, true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race1Done = true; stats1Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<AlgorithmStep> steps = algo2.run(input2);
                time2Micros = (System.nanoTime() - start) / 1_000;

                Platform.runLater(() -> {
                    thread2Label.setText("Thread: " + threadName);
                    playSortingSteps(steps, barVisualizer2, progress2Bar, stats2Label, executionTime2Label, false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race2Done = true; stats2Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
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
                    progressBar.setStyle("-fx-accent: #2ecc71;");
                    if (isFirst) race1Done = true; else race2Done = true;
                    checkRaceFinished();
                }
            });
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    // ---------- GRAPH RACE (user input — waits for a shared source click) ----------

    private void setupGraphSourceSelection(RaceSession session, String name1, String name2) {
        if (session.isCustomGraph()) {
            pendingGraph1 = GraphInputParser.parse(session.getRaceVertexCount(), session.getEdges1Text());
            pendingGraph2 = GraphInputParser.parse(session.getRaceVertexCount(), session.getEdges2Text());
        } else {
            pendingGraph1 = buildExampleGraph();
            pendingGraph2 = buildExampleGraph();
        }

        pendingAlgo1 = createGraphAlgorithm(name1);
        pendingAlgo2 = createGraphAlgorithm(name2);

        if (pendingAlgo1 == null || pendingAlgo2 == null) {
            resultLabel.setText("Error: unrecognized algorithm");
            return;
        }

        timeComplexity1Label.setText("Complexity: " + pendingAlgo1.getTheoreticalComplexity());
        timeComplexity2Label.setText("Complexity: " + pendingAlgo2.getTheoreticalComplexity());
        resultLabel.setText("Click a node on the left graph to choose the shared source vertex");

        buildDistanceTracker(distanceTracker1Box, tracker1Labels, pendingGraph1);
        buildDistanceTracker(distanceTracker2Box, tracker2Labels, pendingGraph2);

        graphVisualizer1.enableSourceSelection(visualizationPane1, pendingGraph1, this::onSharedSourceSelected);
        graphVisualizer2.initialize(visualizationPane2, pendingGraph2);
    }

    private void onSharedSourceSelected(String nodeId) {
        graphVisualizer1.markSource(nodeId);
        graphVisualizer2.markSource(nodeId);
        sourceInstructionLabel.setText("Source: " + nodeId);
        startRaceButton.setVisible(true);
        startRaceButton.setManaged(true);
        startRaceButton.setUserData(nodeId);
    }

    @FXML
    private void handleStartRaceClick() {
        String startNode = (String) startRaceButton.getUserData();
        startRaceButton.setDisable(true);
        resultLabel.setText("Racing...");
        runGraphRaceFrom(startNode);
    }

    private void runGraphRaceFrom(String startNode) {
        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<GraphStep> steps = pendingAlgo1.run(pendingGraph1, startNode);
                time1Micros = (System.nanoTime() - start) / 1_000;

                Platform.runLater(() -> {
                    thread1Label.setText("Thread: " + threadName);
                    playGraphSteps(steps, graphVisualizer1, progress1Bar, stats1Label, executionTime1Label, tracker1Labels, true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race1Done = true; stats1Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<GraphStep> steps = pendingAlgo2.run(pendingGraph2, startNode);
                time2Micros = (System.nanoTime() - start) / 1_000;

                Platform.runLater(() -> {
                    thread2Label.setText("Thread: " + threadName);
                    playGraphSteps(steps, graphVisualizer2, progress2Bar, stats2Label, executionTime2Label, tracker2Labels, false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race2Done = true; stats2Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });
    }

    private void playGraphSteps(List<GraphStep> steps, GraphVisualizer visualizer, ProgressBar progressBar,
                                Label statsLabel, Label timeLabel, Map<String, Label> trackerLabels, boolean isFirst) {
        Timeline timeline = new Timeline();

        for (int i = 0; i < steps.size(); i++) {
            GraphStep step = steps.get(i);
            int stepIndex = i;
            boolean isFinal = stepIndex == steps.size() - 1;

            KeyFrame frame = new KeyFrame(Duration.millis(450.0 * i), event -> {
                visualizer.animateToStep(step, isFinal);
                updateTracker(trackerLabels, step);
                progressBar.setProgress((stepIndex + 1) / (double) steps.size());
                statsLabel.setText("Comparisons: " + step.getComparisons() + " | Relaxations: " + step.getRelaxations());

                if (isFinal) {
                    long micros = isFirst ? time1Micros : time2Micros;
                    timeLabel.setText("Computation Time: " + micros + " Β΅s");
                    progressBar.setStyle("-fx-accent: #2ecc71;");
                    if (isFirst) race1Done = true; else race2Done = true;
                    checkRaceFinished();
                }
            });
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    private void buildDistanceTracker(VBox box, Map<String, Label> labelMap, Graph graph) {
        box.getChildren().clear();
        labelMap.clear();
        for (GraphNode node : graph.getNodes()) {
            Label label = new Label(node.getId() + ": ∞");
            labelMap.put(node.getId(), label);
            box.getChildren().add(label);
        }
    }

    private void updateTracker(Map<String, Label> labelMap, GraphStep step) {
        for (Map.Entry<String, Integer> entry : step.getDistances().entrySet()) {
            Label label = labelMap.get(entry.getKey());
            if (label != null) {
                int dist = entry.getValue();
                label.setText(entry.getKey() + ": " + (dist == Integer.MAX_VALUE ? "∞" : dist));
            }
        }
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

    // ---------- CASE BATTLE (fixed start "0", unaffected by click-selection) ----------

    private void runCaseBattle(RaceSession session) {
        if (session.getCategory().equals("Graph")) {
            runGraphCaseBattle(session);
        } else {
            runSortingCaseBattle(session);
        }
    }

    private void runSortingCaseBattle(RaceSession session) {
        String name1 = session.getAlgorithm1Name();
        String name2 = session.getAlgorithm2Name();
        String case1 = session.getCase1();
        String case2 = session.getCase2();
        int size = session.getBattleInputSize();

        int[] input1 = InputGenerator.generate(name1, case1, size);
        int[] input2 = InputGenerator.generate(name2, case2, size);

        Algorithm algo1 = createSortingAlgorithm(name1);
        Algorithm algo2 = createSortingAlgorithm(name2);

        algorithm1NameLabel.setText(algo1.getName() + " (" + case1 + ")");
        algorithm2NameLabel.setText(algo2.getName() + " (" + case2 + ")");
        timeComplexity1Label.setText("Complexity: " + describeComplexity(name1, case1) + " (" + case1 + ")");
        timeComplexity2Label.setText("Complexity: " + describeComplexity(name2, case2) + " (" + case2 + ")");
        resultLabel.setText("Racing...");

        barVisualizer1.initialize(visualizationPane1, input1);
        barVisualizer2.initialize(visualizationPane2, input2);

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<AlgorithmStep> steps = algo1.run(input1);
                time1Micros = (System.nanoTime() - start) / 1_000;

                AlgorithmStep last = steps.get(steps.size() - 1);
                saveRun(name1, case1, size, steps.size(), last.getComparisons(), last.getSwaps(), time1Micros);

                Platform.runLater(() -> {
                    thread1Label.setText("Thread: " + threadName);
                    playSortingSteps(steps, barVisualizer1, progress1Bar, stats1Label, executionTime1Label, true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race1Done = true; stats1Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<AlgorithmStep> steps = algo2.run(input2);
                time2Micros = (System.nanoTime() - start) / 1_000;

                AlgorithmStep last = steps.get(steps.size() - 1);
                saveRun(name2, case2, size, steps.size(), last.getComparisons(), last.getSwaps(), time2Micros);

                Platform.runLater(() -> {
                    thread2Label.setText("Thread: " + threadName);
                    playSortingSteps(steps, barVisualizer2, progress2Bar, stats2Label, executionTime2Label, false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race2Done = true; stats2Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });
    }

    private void runGraphCaseBattle(RaceSession session) {
        String name1 = session.getAlgorithm1Name();
        String name2 = session.getAlgorithm2Name();
        String case1 = session.getCase1();
        String case2 = session.getCase2();
        int vertexCount = session.getBattleInputSize();

        Graph graph1 = GraphCaseGenerator.generate(case1, vertexCount);
        Graph graph2 = GraphCaseGenerator.generate(case2, vertexCount);

        GraphAlgorithm algo1 = createGraphAlgorithm(name1);
        GraphAlgorithm algo2 = createGraphAlgorithm(name2);

        algorithm1NameLabel.setText(name1 + " (" + case1 + ")");
        algorithm2NameLabel.setText(name2 + " (" + case2 + ")");
        timeComplexity1Label.setText("Complexity: " + algo1.getTheoreticalComplexity());
        timeComplexity2Label.setText("Complexity: " + algo2.getTheoreticalComplexity());
        resultLabel.setText("Racing...");

        graphVisualizer1.initialize(visualizationPane1, graph1);
        graphVisualizer2.initialize(visualizationPane2, graph2);
        buildDistanceTracker(distanceTracker1Box, tracker1Labels, graph1);
        buildDistanceTracker(distanceTracker2Box, tracker2Labels, graph2);

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<GraphStep> steps = algo1.run(graph1, "0");
                time1Micros = (System.nanoTime() - start) / 1_000;

                GraphStep last = steps.get(steps.size() - 1);
                saveGraphRun(name1, case1, vertexCount, steps.size(), last.getComparisons(), last.getRelaxations(), time1Micros);

                Platform.runLater(() -> {
                    thread1Label.setText("Thread: " + threadName);
                    playGraphSteps(steps, graphVisualizer1, progress1Bar, stats1Label, executionTime1Label, tracker1Labels, true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race1Done = true; stats1Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });

        AppExecutor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                long start = System.nanoTime();
                List<GraphStep> steps = algo2.run(graph2, "0");
                time2Micros = (System.nanoTime() - start) / 1_000;

                GraphStep last = steps.get(steps.size() - 1);
                saveGraphRun(name2, case2, vertexCount, steps.size(), last.getComparisons(), last.getRelaxations(), time2Micros);

                Platform.runLater(() -> {
                    thread2Label.setText("Thread: " + threadName);
                    playGraphSteps(steps, graphVisualizer2, progress2Bar, stats2Label, executionTime2Label, tracker2Labels, false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> { race2Done = true; stats2Label.setText("Error: " + e.getMessage()); checkRaceFinished(); });
            }
        });
    }

    private void saveRun(String algorithm, String caseType, int size, int steps, int comparisons, int swaps, long micros) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Run run = new Run(algorithm, caseType, size, steps, comparisons, swaps, micros, timestamp);
        runDAO.insertRun(run);
    }

    private void saveGraphRun(String algorithm, String caseType, int vertexCount, int steps, int comparisons, int relaxations, long micros) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Run run = new Run(algorithm, caseType, vertexCount, steps, comparisons, relaxations, micros, timestamp);
        runDAO.insertRun(run);
    }

    // ---------- SHARED ----------

    private void checkRaceFinished() {
        if (race1Done && race2Done) {
            String name1 = algorithm1NameLabel.getText();
            String name2 = algorithm2NameLabel.getText();
            String prefix = "✅ ";

            if (time1Micros < time2Micros) {
                resultLabel.setText(prefix + name1 + " beat " + name2 + "! (" + time1Micros + "Β΅s vs " + time2Micros + "Β΅s)");
            } else if (time2Micros < time1Micros) {
                resultLabel.setText(prefix + name2 + " beat " + name1 + "! (" + time2Micros + "Β΅s vs " + time1Micros + "Β΅s)");
            } else {
                resultLabel.setText(prefix + "It's a tie! (" + time1Micros + "Β΅s each)");
            }
        }
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
            case "Merge Sort" -> new com.codecanvas.algorithm.sorting.MergeSort();
            default -> null;
        };
    }

    private GraphAlgorithm createGraphAlgorithm(String name) {
        return switch (name) {
            case "Dijkstra" -> new Dijkstra();
            case "Bellman-Ford" -> new BellmanFord();
            case "Prim's Algorithm" -> new com.codecanvas.algorithm.graph.Prims();
            case "Kruskal's Algorithm" -> new com.codecanvas.algorithm.graph.Kruskal();
            default -> null;
        };
    }


}