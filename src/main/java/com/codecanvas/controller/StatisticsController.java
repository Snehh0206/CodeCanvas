package com.codecanvas.controller;

import com.codecanvas.database.RunDAO;
import com.codecanvas.model.Run;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.SceneManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatisticsController extends BaseController implements Initializable {

    @FXML
    private ComboBox<String> algorithmComboBox;
    @FXML
    private LineChart<Number, Number> stepsChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label noteLabel;

    private final RunDAO runDAO = new RunDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Insertion Sort", "Quick Sort", "Dijkstra", "Bellman-Ford"));
        algorithmComboBox.setValue("Insertion Sort");
        loadChartFor("Insertion Sort");
    }

    @FXML
    private void handleAlgorithmChange() {
        String selected = algorithmComboBox.getValue();
        if (selected != null) {
            loadChartFor(selected);
        }
    }

    private void loadChartFor(String algorithm) {
        AppExecutor.submit(() -> {
            List<Run> runs = runDAO.getRunsByAlgorithm(algorithm);

            Platform.runLater(() -> {
                stepsChart.getData().clear();

                if (runs.isEmpty()) {
                    stepsChart.setTitle("No saved runs yet for " + algorithm);
                    return;
                }

                stepsChart.setTitle("Steps vs Input Size — " + algorithm);

                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(algorithm);

                for (Run run : runs) {
                    series.getData().add(new XYChart.Data<>(run.getInputSize(), run.getSteps()));
                }

                stepsChart.getData().add(series);
            });
        });
    }

}