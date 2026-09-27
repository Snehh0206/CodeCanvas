package com.codecanvas.controller;

import com.codecanvas.database.UserDAO;
import com.codecanvas.service.PasswordHasher;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheckBox;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleTogglePassword() {
        boolean show = showPasswordCheckBox.isSelected();
        if (show) visiblePasswordField.setText(passwordField.getText());
        else passwordField.setText(visiblePasswordField.getText());
        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter a username and password").showAndWait();
            return;
        }
        if (userDAO.usernameExists(username)) {
            new Alert(Alert.AlertType.ERROR, "Username already taken").showAndWait();
            return;
        }

        userDAO.registerUser(username, PasswordHasher.hash(password));
        new Alert(Alert.AlertType.INFORMATION, "Account created! Please log in.").showAndWait();
        SceneManager.switchTo("Login.fxml");
    }

    @FXML
    private void handleGoToLogin() {
        SceneManager.switchTo("Login.fxml");
    }
}