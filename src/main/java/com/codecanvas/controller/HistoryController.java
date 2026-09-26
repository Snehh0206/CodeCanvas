package com.codecanvas.controller;

import com.codecanvas.database.RunDAO;
import com.codecanvas.model.Run;
import com.codecanvas.service.AppExecutor;
import com.codecanvas.service.SceneManager;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryController extends BaseController implements Initializable {

    @FXML private TableView<RunRow> historyTable;
    @FXML private TableColumn<RunRow, Integer> idColumn;
    @FXML private TableColumn<RunRow, String> algorithmColumn;
    @FXML private TableColumn<RunRow, String> caseTypeColumn;
    @FXML private TableColumn<RunRow, Integer> inputSizeColumn;
    @FXML private TableColumn<RunRow, Integer> stepsColumn;
    @FXML private TableColumn<RunRow, Integer> comparisonsColumn;
    @FXML private TableColumn<RunRow, Integer> swapsColumn;
    @FXML private TableColumn<RunRow, Long> timeColumn;
    @FXML private TableColumn<RunRow, String> dateColumn;

    private final RunDAO runDAO = new RunDAO();
    private final ObservableList<RunRow> tableData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        historyTable.getColumns().forEach(col -> col.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.11)));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        algorithmColumn.setCellValueFactory(new PropertyValueFactory<>("algorithm"));
        caseTypeColumn.setCellValueFactory(new PropertyValueFactory<>("caseType"));
        inputSizeColumn.setCellValueFactory(new PropertyValueFactory<>("inputSize"));
        stepsColumn.setCellValueFactory(new PropertyValueFactory<>("steps"));
        comparisonsColumn.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        swapsColumn.setCellValueFactory(new PropertyValueFactory<>("swaps"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("executionTimeMicros"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("runDate"));

        historyTable.setItems(tableData);
        loadRuns();
    }

    private void loadRuns() {
        AppExecutor.submit(() -> {
            List<Run> runs = runDAO.getAllRuns();

            Platform.runLater(() -> {
                tableData.clear();
                for (Run run : runs) {
                    tableData.add(new RunRow(run));
                }
            });
        });
    }

    @FXML
    private void handleDelete() {
        RunRow selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a row to delete first").showAndWait();
            return;
        }

        int id = selected.getId();
        AppExecutor.submit(() -> {
            runDAO.deleteRun(id);
            Platform.runLater(this::loadRuns);
        });
    }

    @FXML
    private void handleRefresh() {
        loadRuns();
    }



    // Wraps a Run in JavaFX properties so TableView can bind and display it
    public static class RunRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty algorithm;
        private final SimpleStringProperty caseType;
        private final SimpleIntegerProperty inputSize;
        private final SimpleIntegerProperty steps;
        private final SimpleIntegerProperty comparisons;
        private final SimpleIntegerProperty swaps;
        private final SimpleLongProperty executionTimeMicros;
        private final SimpleStringProperty runDate;

        public RunRow(Run run) {
            this.id = new SimpleIntegerProperty(run.getId());
            this.algorithm = new SimpleStringProperty(run.getAlgorithm());
            this.caseType = new SimpleStringProperty(run.getCaseType());
            this.inputSize = new SimpleIntegerProperty(run.getInputSize());
            this.steps = new SimpleIntegerProperty(run.getSteps());
            this.comparisons = new SimpleIntegerProperty(run.getComparisons());
            this.swaps = new SimpleIntegerProperty(run.getSwaps());
            this.executionTimeMicros = new SimpleLongProperty(run.getExecutionTimeMicros());
            this.runDate = new SimpleStringProperty(run.getRunDate());
        }

        public int getId() { return id.get(); }
        public String getAlgorithm() { return algorithm.get(); }
        public String getCaseType() { return caseType.get(); }
        public int getInputSize() { return inputSize.get(); }
        public int getSteps() { return steps.get(); }
        public int getComparisons() { return comparisons.get(); }
        public int getSwaps() { return swaps.get(); }
        public long getExecutionTimeMicros() { return executionTimeMicros.get(); }
        public String getRunDate() { return runDate.get(); }
    }
}