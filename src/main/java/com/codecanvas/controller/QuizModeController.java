package com.codecanvas.controller;

import com.codecanvas.database.QuizResultDAO;
import com.codecanvas.model.QuizQuestion;
import com.codecanvas.model.QuizResult;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.QuizBankFetcher;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuizModeController extends BaseController implements Initializable {

    private static final int QUESTIONS_PER_QUIZ = 5;

    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Label sourceLabel;
    @FXML private Label progressLabel;
    @FXML private Label questionLabel;
    @FXML private Label feedbackLabel;
    @FXML private Label scoreLabel;
    @FXML private VBox optionsBox;
    @FXML private Button submitButton;

    private final QuizResultDAO quizResultDAO = new QuizResultDAO();
    private List<QuizQuestion> questions = new ArrayList<>();
    private ToggleGroup group;
    private String algorithmName;
    private int index;
    private int score;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Insertion Sort", "Quick Sort", "Merge Sort", "Dijkstra", "Bellman-Ford",
                "Prim's Algorithm", "Kruskal's Algorithm", "Floyd-Warshall", "BFS", "DFS"));
    }

    @FXML
    private void handleStartQuiz() {
        algorithmName = algorithmComboBox.getValue();
        if (algorithmName == null) {
            new Alert(Alert.AlertType.WARNING, "Pick an algorithm first").showAndWait();
            return;
        }

        questionLabel.setText("Loading questions...");
        sourceLabel.setText("");
        progressLabel.setText("");
        scoreLabel.setText("");
        optionsBox.getChildren().clear();
        setVisible(submitButton, false);
        setVisible(feedbackLabel, false);

        AppExecutor.submit(() -> {
            try {
                QuizBankFetcher.QuizBank bank = QuizBankFetcher.load(algorithmName);
                Platform.runLater(() -> startWith(bank));
            } catch (Exception e) {
                Platform.runLater(() -> questionLabel.setText("Could not load questions: " + e.getMessage()));
            }
        });
    }

    private void startWith(QuizBankFetcher.QuizBank bank) {
        sourceLabel.setText("Questions source: " + bank.source);
        if (bank.questions.isEmpty()) {
            questionLabel.setText("No questions found for " + algorithmName);
            return;
        }
        int count = Math.min(QUESTIONS_PER_QUIZ, bank.questions.size());
        questions = new ArrayList<>(bank.questions.subList(0, count));
        index = 0;
        score = 0;
        showQuestion();
    }

    private void showQuestion() {
        QuizQuestion q = questions.get(index);
        progressLabel.setText("QUESTION " + (index + 1) + " OF " + questions.size());
        questionLabel.setText(q.getQuestionText());

        optionsBox.getChildren().clear();
        group = new ToggleGroup();
        for (String option : q.getOptions()) {
            RadioButton rb = new RadioButton(option);
            rb.setToggleGroup(group);
            rb.setWrapText(true);
            rb.setMaxWidth(580);
            optionsBox.getChildren().add(rb);
        }
        setVisible(submitButton, true);
    }

    @FXML
    private void handleSubmit() {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pick an answer first").showAndWait();
            return;
        }

        QuizQuestion q = questions.get(index);
        boolean correct = optionsBox.getChildren().indexOf(selected) == q.getCorrectOptionIndex();
        if (correct) score++;

        feedbackLabel.setText(correct ? "Correct!"
                : "Not quite. Correct answer: " + q.getOptions().get(q.getCorrectOptionIndex()));
        setVisible(feedbackLabel, true);

        index++;
        if (index < questions.size()) showQuestion();
        else finishQuiz();
    }

    private void finishQuiz() {
        questionLabel.setText("Quiz complete!");
        progressLabel.setText("");
        optionsBox.getChildren().clear();
        setVisible(submitButton, false);
        scoreLabel.setText("Score: " + score + " / " + questions.size());

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        QuizResult result = new QuizResult(null, algorithmName, score, questions.size(), timestamp);
        AppExecutor.submit(() -> quizResultDAO.insertResult(result));
    }

    private void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}