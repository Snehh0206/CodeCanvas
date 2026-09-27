package com.codecanvas.controller;

import com.codecanvas.database.UserDAO;
import com.codecanvas.model.UserSession;
import com.codecanvas.service.PasswordHasher;
import com.codecanvas.service.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

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
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter username and password").showAndWait();
            return;
        }

        int userId = userDAO.login(username, PasswordHasher.hash(password));
        if (userId == -1) {
            new Alert(Alert.AlertType.ERROR, "Invalid username or password").showAndWait();
            return;
        }

        UserSession.getInstance().setUserId(userId);
        UserSession.getInstance().setUsername(username);
        SceneManager.goToDashboard();
    }

    @FXML
    private void handleGoToRegister() {
        SceneManager.switchTo("Register.fxml");
    }
}