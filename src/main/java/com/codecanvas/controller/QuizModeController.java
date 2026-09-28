package com.codecanvas.controller;

import com.codecanvas.algorithm.Algorithm;
import com.codecanvas.algorithm.GraphAlgorithm;
import com.codecanvas.algorithm.graph.BellmanFord;
import com.codecanvas.algorithm.graph.Dijkstra;
import com.codecanvas.algorithm.graph.Kruskal;
import com.codecanvas.algorithm.graph.Prims;
import com.codecanvas.algorithm.sorting.InsertionSort;
import com.codecanvas.algorithm.sorting.MergeSort;
import com.codecanvas.algorithm.sorting.QuickSort;
import com.codecanvas.database.QuizResultDAO;
import com.codecanvas.model.*;
import com.codecanvas.service.LiveDataFetcher;
import com.codecanvas.service.QuizGenerator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuizModeController extends BaseController implements Initializable {

    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Label questionLabel;
    @FXML private VBox optionsBox;
    @FXML private Button submitButton;
    @FXML private Label scoreLabel;

    private final QuizResultDAO quizResultDAO = new QuizResultDAO();
    private List<AlgorithmStep> sortSteps;
    private List<GraphStep> graphSteps;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 0;
    private QuizQuestion currentQuestion;
    private ToggleGroup toggleGroup;
    private boolean isGraphQuiz;
    private String algorithmName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Insertion Sort", "Quick Sort", "Merge Sort", "Dijkstra", "Bellman-Ford", "Prim's Algorithm", "Kruskal's Algorithm"));
    }

    @FXML
    private void handleStartQuiz() {
        algorithmName = algorithmComboBox.getValue();
        if (algorithmName == null) {
            new Alert(Alert.AlertType.ERROR, "Pick an algorithm first").showAndWait();
            return;
        }

        isGraphQuiz = algorithmName.equals("Dijkstra") || algorithmName.equals("Bellman-Ford")
                || algorithmName.equals("Prim's Algorithm") || algorithmName.equals("Kruskal's Algorithm");

        questionLabel.setText("Fetching live data...");
        new Thread(() -> {
            try {
                if (isGraphQuiz) {
                    Graph liveGraph = com.codecanvas.service.GraphLiveDataFetcher.fetchLiveGraph();
                    GraphAlgorithm algo = switch (algorithmName) {
                        case "Dijkstra" -> new Dijkstra();
                        case "Bellman-Ford" -> new BellmanFord();
                        case "Prim's Algorithm" -> new Prims();
                        default -> new Kruskal();
                    };
                    graphSteps = algo.run(liveGraph, "0");
                } else {
                    int[] liveData = LiveDataFetcher.fetchLiveNumbers();
                    Algorithm algo = switch (algorithmName) {
                        case "Insertion Sort" -> new InsertionSort();
                        case "Quick Sort" -> new QuickSort();
                        default -> new MergeSort();
                    };
                    sortSteps = algo.run(liveData);
                }

                Platform.runLater(() -> {
                    currentQuestionIndex = 0;
                    score = 0;
                    totalQuestions = 0;
                    scoreLabel.setText("");
                    askNextQuestion();
                });
            } catch (Exception e) {
                Platform.runLater(() -> questionLabel.setText("Couldn't fetch live data — check internet connection"));
            }
        }).start();
    }

    private void askNextQuestion() {
        List<?> steps = isGraphQuiz ? graphSteps : sortSteps;
        if (steps == null || currentQuestionIndex >= steps.size() - 1 || totalQuestions >= 5) {
            finishQuiz();
            return;
        }

        if (isGraphQuiz) {
            currentQuestion = QuizGenerator.buildGraphQuestion(graphSteps.get(currentQuestionIndex), graphSteps.get(currentQuestionIndex + 1));
        } else {
            currentQuestion = QuizGenerator.buildQuestion(sortSteps.get(currentQuestionIndex), sortSteps.get(currentQuestionIndex + 1));
        }

        questionLabel.setText(currentQuestion.getQuestionText());
        optionsBox.getChildren().clear();
        toggleGroup = new ToggleGroup();

        for (String option : currentQuestion.getOptions()) {
            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(toggleGroup);
            optionsBox.getChildren().add(rb);
        }

        submitButton.setVisible(true);
        submitButton.setManaged(true);
        currentQuestionIndex += 3; // space questions out across the run
    }

    @FXML
    private void handleSubmit() {
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pick an answer first").showAndWait();
            return;
        }

        totalQuestions++;
        int selectedIndex = optionsBox.getChildren().indexOf(selected);
        if (selectedIndex == currentQuestion.getCorrectOptionIndex()) {
            score++;
        }

        askNextQuestion();
    }

    private void finishQuiz() {
        questionLabel.setText("Quiz complete!");
        optionsBox.getChildren().clear();
        submitButton.setVisible(false);
        submitButton.setManaged(false);
        scoreLabel.setText("Score: " + score + "/" + totalQuestions);

        if (totalQuestions > 0) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            QuizResult result = new QuizResult(null, algorithmName, score, totalQuestions, timestamp);
            new Thread(() -> quizResultDAO.insertResult(result)).start();
        }
    }
}