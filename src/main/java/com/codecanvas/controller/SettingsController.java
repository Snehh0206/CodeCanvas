package com.codecanvas.controller;

import com.codecanvas.model.AppSettings;
import com.codecanvas.service.SceneManager;
import com.codecanvas.service.SoundManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends BaseController implements Initializable {

    @FXML private RadioButton lightModeRadio;
    @FXML private RadioButton darkModeRadio;
    @FXML private CheckBox soundCheckBox;
    @FXML private CheckBox animationCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppSettings settings = AppSettings.getInstance();

        if (settings.isDarkMode()) {
            darkModeRadio.setSelected(true);
        } else {
            lightModeRadio.setSelected(true);
        }

        soundCheckBox.setSelected(settings.isSoundEnabled());
        animationCheckBox.setSelected(settings.isAnimationEnabled());
    }

    @FXML
    private void handleSave() {
        AppSettings settings = AppSettings.getInstance();

        settings.setDarkMode(darkModeRadio.isSelected());
        settings.setSoundEnabled(soundCheckBox.isSelected());
        settings.setAnimationEnabled(animationCheckBox.isSelected());

        SoundManager.setEnabled(soundCheckBox.isSelected());
        SceneManager.refreshTheme();
    }


}