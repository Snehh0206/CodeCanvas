package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.algorithm.graph.BellmanFord;
import com.codecanvas.algorithm.graph.Dijkstra;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.database.QuizResultDAO;
import com.codecanvas.database.RunDAO;
import com.codecanvas.model.*;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.PseudocodeProvider;
import com.codecanvas.service.QuizGenerator;
import com.codecanvas.visualization.BarVisualizer;
import com.codecanvas.visualization.GraphVisualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AlgorithmLabController extends BaseController implements Initializable {

    @FXML private Pane visualizationPane;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button restartButton;
    @FXML private Slider speedSlider;
    @FXML private RadioButton slowSpeedRadio;
    @FXML private RadioButton mediumSpeedRadio;
    @FXML private RadioButton fastSpeedRadio;
    @FXML private Label currentStepLabel;
    @FXML private Label totalStepsLabel;
    @FXML private Label comparisonsLabel;
    @FXML private Label swapsLabel;
    @FXML private Label executionTimeLabel;
    @FXML private Label theoreticalComplexityLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label narrationLabel;
    @FXML private Label completionLabel;
    @FXML private Label sourceInstructionLabel;
    @FXML private VBox pseudocodeBox;
    @FXML private HBox distanceTrackerBox;
    @FXML private VBox trackerCard;


    private int[] currentInput = {5, 2, 9, 1, 5, 6};
    private int currentStepIndex = 0;
    private Timeline playTimeline;

    private final BarVisualizer visualizer = new BarVisualizer();
    private List<AlgorithmStep> currentSteps;
    private Algorithm currentAlgorithm;

    private final GraphVisualizer graphVisualizer = new GraphVisualizer();
    private List<GraphStep> currentGraphSteps;
    private Graph currentGraph;
    private GraphAlgorithm currentGraphAlgorithm;
    private final Map<String, Label> trackerLabels = new HashMap<>();

    private final RunDAO runDAO = new RunDAO();
    private final QuizResultDAO quizResultDAO = new QuizResultDAO();
    private int lastRunId = -1;
    private int quizScore = 0;
    private int quizTotal = 0;

    private final List<Label> pseudocodeLabels = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStart();
    }

    private void setupStart() {
        quizScore = 0;
        quizTotal = 0;
        lastRunId = -1;
        completionLabel.setVisible(false);
        progressBar.setStyle("");
        sourceInstructionLabel.setText("");
        if (playTimeline != null) playTimeline.stop();

        AlgorithmSession session = AlgorithmSession.getInstance();
        String selected = session.getSelectedAlgorithm();

        if (selected == null) {
            System.out.println("No algorithm selected");
            return;
        }

        loadPseudocode(selected);
        boolean isGraph = isGraphAlgorithmName(selected);
        trackerCard.setVisible(isGraph);
        trackerCard.setManaged(isGraph);
        currentSteps = null;
        currentGraphSteps = null;

        if (isGraphAlgorithmName(selected)) {
            currentGraphAlgorithm = createGraphAlgorithm(selected);

            if (session.isCustomGraph()) {
                currentGraph = GraphInputParser.parse(session.getVertexCount(), session.getEdgesText());
            } else {
                currentGraph = buildExampleGraph();
            }

            buildDistanceTracker(currentGraph);

            boolean needsSource = !(selected.equals("Kruskal's Algorithm") || selected.equals("Floyd-Warshall"));

            if (needsSource) {
                setPlaybackDisabled(true);
                sourceInstructionLabel.setText("Click a node to choose the starting vertex");
                graphVisualizer.enableSourceSelection(visualizationPane, currentGraph, this::onSourceSelected);
            } else {
                graphVisualizer.initialize(visualizationPane, currentGraph);
                sourceInstructionLabel.setText("");
                setPlaybackDisabled(true);
                AppExecutor.submit(() -> {
                    List<GraphStep> computedSteps = currentGraphAlgorithm.run(currentGraph, "0");
                    Platform.runLater(() -> {
                        currentGraphSteps = computedSteps;
                        currentStepIndex = 0;
                        setPlaybackDisabled(false);
                        renderCurrentGraphStep();
                    });
                });
            }
            if (session.getLiveGraph() != null) {
                currentGraph = session.getLiveGraph();
                session.setLiveGraph(null); // consume it once
            } else if (session.isCustomGraph()) {
                currentGraph = GraphInputParser.parse(session.getVertexCount(), session.getEdgesText());
            } else {
                currentGraph = buildExampleGraph();
            }
            return;
        }

        currentAlgorithm = switch (selected) {
            case "Insertion Sort" -> new InsertionSort();
            case "Quick Sort" -> new QuickSort();
            case "Merge Sort" -> new com.codecanvas.algorithm.sorting.MergeSort();
            default -> null;
        };

        if (currentAlgorithm == null) return;

        setPlaybackDisabled(true);
        AppExecutor.submit(() -> {
            List<AlgorithmStep> computedSteps = currentAlgorithm.run(currentInput);

            Platform.runLater(() -> {
                currentSteps = computedSteps;
                currentStepIndex = 0;
                visualizer.initialize(visualizationPane, currentInput);
                setPlaybackDisabled(false);
                renderCurrentStep();
            });
        });
    }
    private boolean isGraphAlgorithmName(String name) {
        return name.equals("Dijkstra") || name.equals("Bellman-Ford") || name.equals("Prim's Algorithm")
                || name.equals("Kruskal's Algorithm") || name.equals("Floyd-Warshall")
                || name.equals("BFS") || name.equals("DFS");
    }

    private GraphAlgorithm createGraphAlgorithm(String name) {
        return switch (name) {
            case "Dijkstra" -> new Dijkstra();
            case "Bellman-Ford" -> new BellmanFord();
            case "Prim's Algorithm" -> new com.codecanvas.algorithm.graph.Prims();
            case "Kruskal's Algorithm" -> new com.codecanvas.algorithm.graph.Kruskal();
            case "Floyd-Warshall" -> new com.codecanvas.algorithm.graph.FloydWarshall();
            case "BFS" -> new com.codecanvas.algorithm.graph.BFS();
            case "DFS" -> new com.codecanvas.algorithm.graph.DFS();
            default -> null;
        };
    }

    // Fires once the user clicks a node on the gray graph — that click IS the source pick
    private void onSourceSelected(String nodeId) {
        graphVisualizer.markSource(nodeId);
        sourceInstructionLabel.setText("Source: " + nodeId);

        AppExecutor.submit(() -> {
            List<GraphStep> computedSteps = currentGraphAlgorithm.run(currentGraph, nodeId);

            Platform.runLater(() -> {
                currentGraphSteps = computedSteps;
                currentStepIndex = 0;
                setPlaybackDisabled(false);
                renderCurrentGraphStep();
            });
        });
    }

    private void setPlaybackDisabled(boolean disabled) {
        startButton.setDisable(disabled);
        pauseButton.setDisable(disabled);
        previousButton.setDisable(disabled);
        nextButton.setDisable(disabled);
        restartButton.setDisable(disabled);
    }

    private void buildDistanceTracker(Graph graph) {
        distanceTrackerBox.getChildren().clear();
        trackerLabels.clear();
        for (GraphNode node : graph.getNodes()) {
            Label label = new Label(node.getId() + ": ∞");
            label.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 6 10; -fx-background-radius: 6; -fx-font-weight: bold;");
            trackerLabels.put(node.getId(), label);
            distanceTrackerBox.getChildren().add(label);
        }
    }

    private void updateDistanceTracker(GraphStep step) {
        for (Map.Entry<String, Integer> entry : step.getDistances().entrySet()) {
            Label label = trackerLabels.get(entry.getKey());
            if (label != null) {
                int dist = entry.getValue();
                label.setText(entry.getKey() + ": " + (dist == Integer.MAX_VALUE ? "∞" : dist));
            }
        }
    }

    @FXML
    private void handleSpeedRadio() {
        if (slowSpeedRadio.isSelected()) speedSlider.setValue(2);
        else if (fastSpeedRadio.isSelected()) speedSlider.setValue(9);
        else speedSlider.setValue(5);
    }

    @FXML
    private void handleStart() {
        startAutoPlay();
    }

    @FXML
    private void handlePause() {
        if (playTimeline != null) playTimeline.stop();
    }

    private void startAutoPlay() {
        if (playTimeline != null) playTimeline.stop();
        double speed = speedSlider.getValue();
        double intervalMs = 1100 - (speed * 100);

        playTimeline = new Timeline(new KeyFrame(Duration.millis(intervalMs), e -> {
            boolean advanced = advanceOneStep();
            if (!advanced) playTimeline.stop();
        }));
        playTimeline.setCycleCount(Timeline.INDEFINITE);
        playTimeline.play();
    }

    private boolean advanceOneStep() {
        if (currentGraphSteps != null) {
            if (currentStepIndex < currentGraphSteps.size() - 1) {
                currentStepIndex++;
                renderCurrentGraphStep();
                return true;
            }
            return false;
        } else if (currentSteps != null) {
            if (currentStepIndex < currentSteps.size() - 1) {
                currentStepIndex++;
                renderCurrentStep();
                return true;
            }
            return false;
        }
        return false;
    }

    @FXML
    private void handleNext() {
        if (playTimeline != null) playTimeline.stop();
        advanceOneStep();
    }

    @FXML
    private void handlePrevious() {
        if (playTimeline != null) playTimeline.stop();
        if (currentGraphSteps != null) {
            if (currentStepIndex > 0) { currentStepIndex--; renderCurrentGraphStep(); }
        } else if (currentSteps != null) {
            if (currentStepIndex > 0) { currentStepIndex--; renderCurrentStep(); }
        }
    }

    @FXML
    private void handleRestart() {
        if (playTimeline != null) playTimeline.stop();
        currentStepIndex = 0;
        completionLabel.setVisible(false);
        progressBar.setStyle("");
        if (currentGraphSteps != null) renderCurrentGraphStep();
        else if (currentSteps != null) renderCurrentStep();
    }

    private void renderCurrentStep() {
        AlgorithmStep step = currentSteps.get(currentStepIndex);
        visualizer.animateToStep(step);

        currentStepLabel.setText("Step: " + (currentStepIndex + 1));
        totalStepsLabel.setText("Total Steps: " + currentSteps.size());
        comparisonsLabel.setText("Comparisons: " + step.getComparisons());
        swapsLabel.setText("Swaps: " + step.getSwaps());
        theoreticalComplexityLabel.setText("Theoretical: " + currentAlgorithm.getTheoreticalComplexity());
        progressBar.setProgress((currentStepIndex + 1) / (double) currentSteps.size());
        narrationLabel.setText(step.getDescription());
        highlightLine(step.getCurrentLine());

        boolean quizOn = AlgorithmSession.getInstance().isQuizMode();
        boolean everyThirdStep = (currentStepIndex + 1) % 3 == 0;
        boolean hasNextStep = currentStepIndex < currentSteps.size() - 1;

        if (quizOn && everyThirdStep && hasNextStep) {
            if (playTimeline != null) playTimeline.stop();
            showSortQuizPopup(step, currentSteps.get(currentStepIndex + 1));
        }

        if (currentStepIndex == currentSteps.size() - 1) {
            showCompletion("✅ Sorting Complete!");
            saveRunToDatabase(currentAlgorithm.getName(), currentInput.length, currentSteps.size(),
                    step.getComparisons(), step.getSwaps());
            if (quizOn && quizTotal > 0) saveQuizResult(currentAlgorithm.getName());
        }
    }

    private void renderCurrentGraphStep() {
        GraphStep step = currentGraphSteps.get(currentStepIndex);
        boolean isFinal = currentStepIndex == currentGraphSteps.size() - 1;
        graphVisualizer.animateToStep(step, isFinal);
        updateDistanceTracker(step);

        currentStepLabel.setText("Step: " + (currentStepIndex + 1));
        totalStepsLabel.setText("Total Steps: " + currentGraphSteps.size());
        comparisonsLabel.setText("Comparisons: " + step.getComparisons());
        swapsLabel.setText("Relaxations: " + step.getRelaxations());
        theoreticalComplexityLabel.setText("Theoretical: " + currentGraphAlgorithm.getTheoreticalComplexity());
        progressBar.setProgress((currentStepIndex + 1) / (double) currentGraphSteps.size());
        narrationLabel.setText(step.getDescription());
        highlightLine(step.getCurrentLine());

        boolean quizOn = AlgorithmSession.getInstance().isQuizMode();
        boolean everyThirdStep = (currentStepIndex + 1) % 3 == 0;
        boolean hasNextStep = currentStepIndex < currentGraphSteps.size() - 1;

        if (quizOn && everyThirdStep && hasNextStep) {
            if (playTimeline != null) playTimeline.stop();
            showGraphQuizPopup(step, currentGraphSteps.get(currentStepIndex + 1));
        }

        if (isFinal) {
            showCompletion("✅ Shortest paths found!");
            narrationLabel.setText(buildDistanceSummary(step));
            saveGraphRunToDatabase(currentGraphAlgorithm.getName(), currentGraph.getNodes().size(),
                    currentGraphSteps.size(), step.getComparisons(), step.getRelaxations());
            if (quizOn && quizTotal > 0) saveQuizResult(currentGraphAlgorithm.getName());
        }
    }

    private void showCompletion(String message) {
        completionLabel.setText(message);
        completionLabel.setVisible(true);
        progressBar.setStyle("-fx-accent: #2ecc71;");
    }

    private void showSortQuizPopup(AlgorithmStep currentStep, AlgorithmStep nextStep) {
        QuizQuestion question = QuizGenerator.buildQuestion(currentStep, nextStep);
        showQuizDialog(question);
    }

    private void showGraphQuizPopup(GraphStep currentStep, GraphStep nextStep) {
        QuizQuestion question = QuizGenerator.buildGraphQuestion(currentStep, nextStep);
        showQuizDialog(question);
    }

    private void showQuizDialog(QuizQuestion question) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Quiz");
        dialog.setHeaderText(question.getQuestionText());

        ToggleGroup group = new ToggleGroup();
        VBox optionsBox = new VBox(8);
        List<RadioButton> radioButtons = new ArrayList<>();

        for (String option : question.getOptions()) {
            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(group);
            radioButtons.add(rb);
            optionsBox.getChildren().add(rb);
        }

        dialog.getDialogPane().setContent(optionsBox);
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(submitButtonType);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == submitButtonType) {
                int selectedIndex = -1;
                for (int i = 0; i < radioButtons.size(); i++) {
                    if (radioButtons.get(i).isSelected()) { selectedIndex = i; break; }
                }

                quizTotal++;
                if (selectedIndex == question.getCorrectOptionIndex()) {
                    quizScore++;
                    new Alert(Alert.AlertType.INFORMATION, "Correct!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Not quite — correct answer was: " + question.getOptions().get(question.getCorrectOptionIndex()))
                            .showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void saveRunToDatabase(String algorithmName, int inputSize, int steps, int comparisons, int swaps) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Run run = new Run(algorithmName, "Custom", inputSize, steps, comparisons, swaps, 0, timestamp);
        lastRunId = runDAO.insertRun(run);
    }

    private void saveGraphRunToDatabase(String algorithmName, int vertexCount, int steps, int comparisons, int relaxations) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Run run = new Run(algorithmName, "Custom", vertexCount, steps, comparisons, relaxations, 0, timestamp);
        lastRunId = runDAO.insertRun(run);
    }

    private void saveQuizResult(String algorithmName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Integer runId = lastRunId != -1 ? lastRunId : null;
        QuizResult result = new QuizResult(runId, algorithmName, quizScore, quizTotal, timestamp);
        AppExecutor.submit(() -> quizResultDAO.insertResult(result));
    }

    private void loadPseudocode(String algorithmName) {
        pseudocodeBox.getChildren().clear();
        pseudocodeLabels.clear();

        for (String line : PseudocodeProvider.getLines(algorithmName)) {
            Label label = new Label(line.isEmpty() ? " " : line);
            label.setWrapText(true);
            label.setMaxWidth(250);
            label.setStyle("-fx-font-family: monospace;");
            pseudocodeLabels.add(label);
            pseudocodeBox.getChildren().add(label);
        }
    }

    private void highlightLine(int lineNumber) {
        for (int i = 0; i < pseudocodeLabels.size(); i++) {
            if (i == lineNumber - 1) {
                pseudocodeLabels.get(i).setStyle("-fx-font-family: monospace; -fx-background-color: #ffe58a; -fx-text-fill: #1b0c1a;");
            } else {
                pseudocodeLabels.get(i).setStyle("-fx-font-family: monospace;");
            }
        }
    }
    private String buildDistanceSummary(GraphStep step) {
        StringBuilder sb = new StringBuilder("Final shortest distances: ");
        for (GraphNode node : currentGraph.getNodes()) {
            Integer dist = step.getDistances().get(node.getId());
            sb.append(node.getId()).append("=").append(dist == null || dist == Integer.MAX_VALUE ? "∞" : dist).append("  ");
        }
        return sb.toString();
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
}