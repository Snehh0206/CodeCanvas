package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.algorithm.graph.BellmanFord;
import com.codecanvas.algorithm.graph.Dijkstra;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.model.AlgorithmSession;
import com.codecanvas.model.AlgorithmStep;
import com.codecanvas.model.Graph;
import com.codecanvas.model.GraphEdge;
import com.codecanvas.model.GraphNode;
import com.codecanvas.model.GraphStep;
import com.codecanvas.visualization.BarVisualizer;
import com.codecanvas.visualization.GraphVisualizer;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.GraphInputParser;
import com.codecanvas.database.RunDAO;
import com.codecanvas.model.Run;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AlgorithmLabController implements Initializable {

  //  @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Pane visualizationPane;
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button restartButton;
    @FXML private Slider speedSlider;
    @FXML private CheckBox quizModeCheckBox;
    @FXML private Label currentStepLabel;
    @FXML private Label totalStepsLabel;
    @FXML private Label comparisonsLabel;
    @FXML private Label swapsLabel;
    @FXML private Label executionTimeLabel;
    @FXML private Label theoreticalComplexityLabel;
    @FXML private ProgressBar progressBar;
    private final RunDAO runDAO = new RunDAO();

    private int[] currentInput = {5, 2, 9, 1, 5, 6};
    private int currentStepIndex = 0;

    // Sorting
    private final BarVisualizer visualizer = new BarVisualizer();
    private List<AlgorithmStep> currentSteps;
    private Algorithm currentAlgorithm;

    // Graph
    private final GraphVisualizer graphVisualizer = new GraphVisualizer();
    private List<GraphStep> currentGraphSteps;
    private Graph currentGraph;
    private GraphAlgorithm currentGraphAlgorithm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AlgorithmSession session = AlgorithmSession.getInstance();

        //algorithmComboBox.setValue(session.getSelectedAlgorithm());

        if (session.isCustomInput() && session.getCustomValues() != null) {
            currentInput = session.getCustomValues();
        }

        runSelectedAlgorithm();
    }

    @FXML
    private void handleStart() {
        runSelectedAlgorithm();
    }

    private void runSelectedAlgorithm() {
        AlgorithmSession session = AlgorithmSession.getInstance();
        String selected = session.getSelectedAlgorithm();

        if (selected == null) {
            System.out.println("No algorithm selected");
            return;
        }

        currentSteps = null;
        currentGraphSteps = null;

        if (selected.equals("Dijkstra") || selected.equals("Bellman-Ford")) {
            currentGraphAlgorithm = selected.equals("Dijkstra") ? new Dijkstra() : new BellmanFord();

            String startNode;
            if (session.isCustomGraph()) {
                currentGraph = GraphInputParser.parse(session.getVertexCount(), session.getEdgesText());
                startNode = "0";
            } else {
                currentGraph = buildExampleGraph();
                startNode = "A";
            }

            String finalStartNode = startNode;
            AppExecutor.submit(() -> {
                List<AlgorithmStep> computedSteps = currentAlgorithm.run(currentInput);

                AlgorithmStep lastStep = computedSteps.get(computedSteps.size() - 1);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Run run = new Run(currentAlgorithm.getName(), "Custom", currentInput.length,
                        computedSteps.size(), lastStep.getComparisons(), lastStep.getSwaps(), 0, timestamp);
                runDAO.insertRun(run);

                Platform.runLater(() -> {
                    currentSteps = computedSteps;
                    currentStepIndex = 0;
                    visualizer.initialize(visualizationPane, currentInput);
                    renderCurrentStep();
                });
            });

            return;
        }

        currentAlgorithm = switch (selected) {
            case "Insertion Sort" -> new InsertionSort();
            case "Quick Sort" -> new QuickSort();
            default -> null;
        };

        if (currentAlgorithm == null) return;

        AppExecutor.submit(() -> {
            List<AlgorithmStep> computedSteps = currentAlgorithm.run(currentInput);

            Platform.runLater(() -> {
                currentSteps = computedSteps;
                currentStepIndex = 0;
                visualizer.initialize(visualizationPane, currentInput);
                renderCurrentStep();
            });
        });
    }

    @FXML
    private void handleNext() {
        if (currentGraphSteps != null) {
            if (currentStepIndex < currentGraphSteps.size() - 1) {
                currentStepIndex++;
                renderCurrentGraphStep();
            }
        } else if (currentSteps != null) {
            if (currentStepIndex < currentSteps.size() - 1) {
                currentStepIndex++;
                renderCurrentStep();
            }
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentGraphSteps != null) {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                renderCurrentGraphStep();
            }
        } else if (currentSteps != null) {
            if (currentStepIndex > 0) {
                currentStepIndex--;
                renderCurrentStep();
            }
        }
    }

    @FXML
    private void handleRestart() {
        if (currentGraphSteps != null) {
            currentStepIndex = 0;
            renderCurrentGraphStep();
        } else if (currentSteps != null) {
            currentStepIndex = 0;
            renderCurrentStep();
        }
    }
    @FXML
    private void handlePause() {
        System.out.println("Pause not implemented yet");
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
    }

    private void renderCurrentGraphStep() {
        GraphStep step = currentGraphSteps.get(currentStepIndex);
        graphVisualizer.animateToStep(step);

        currentStepLabel.setText("Step: " + (currentStepIndex + 1));
        totalStepsLabel.setText("Total Steps: " + currentGraphSteps.size());
        comparisonsLabel.setText("Comparisons: " + step.getComparisons());
        swapsLabel.setText("Relaxations: " + step.getRelaxations());
        theoreticalComplexityLabel.setText("Theoretical: " + currentGraphAlgorithm.getTheoreticalComplexity());
        progressBar.setProgress((currentStepIndex + 1) / (double) currentGraphSteps.size());
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