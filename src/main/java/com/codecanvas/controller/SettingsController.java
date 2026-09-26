package com.codecanvas.controller;

import com.codecanvas.model.AppSettings;
import com.codecanvas.service.SceneManager;
import com.codecanvas.service.SoundManager;
import com.codecanvas.database.AppSettingsDAO;
import com.codecanvas.service.AppExecutor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends BaseController implements Initializable {

    @FXML private RadioButton lightModeRadio;
    @FXML private RadioButton darkModeRadio;
    @FXML private CheckBox soundCheckBox;
    @FXML private CheckBox animationCheckBox;

    @FXML private ComboBox<String> difficultyComboBox;

    private final AppSettingsDAO settingsDAO = new AppSettingsDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppSettings settings = AppSettings.getInstance();

        if (settings.isDarkMode()) darkModeRadio.setSelected(true);
        else lightModeRadio.setSelected(true);

        soundCheckBox.setSelected(settings.isSoundEnabled());
        animationCheckBox.setSelected(settings.isAnimationEnabled());

        difficultyComboBox.setItems(FXCollections.observableArrayList("easy", "medium", "hard"));
        difficultyComboBox.setValue(settings.getQuizDifficulty());
    }

    @FXML
    private void handleSave() {
        AppSettings settings = AppSettings.getInstance();

        settings.setDarkMode(darkModeRadio.isSelected());
        settings.setSoundEnabled(soundCheckBox.isSelected());
        settings.setAnimationEnabled(animationCheckBox.isSelected());
        settings.setQuizDifficulty(difficultyComboBox.getValue());

        SoundManager.setEnabled(soundCheckBox.isSelected());
        SceneManager.refreshTheme();

        String theme = darkModeRadio.isSelected() ? "dark" : "light";
        AppExecutor.submit(() ->
                settingsDAO.updateSettings(theme, difficultyComboBox.getValue(), settings.getDefaultSpeed()));
    }

}