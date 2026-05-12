package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SigninController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    public void initialize() {
        if (visiblePasswordField != null && passwordField != null) {
            visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        boolean show = showPasswordCheckBox.isSelected();

        visiblePasswordField.setVisible(show);
        visiblePasswordField.setManaged(show);

        passwordField.setVisible(!show);
        passwordField.setManaged(!show);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        String password;
        if (showPasswordCheckBox != null && showPasswordCheckBox.isSelected()) {
            password = visiblePasswordField.getText() == null ? "" : visiblePasswordField.getText().trim();
        } else {
            password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        }

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter email and password.");
            return;
        }

        String sql = "SELECT role FROM AppUser WHERE email = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");

                if ("ADMIN".equalsIgnoreCase(role)) {
                    Navigation.goTo(event, "DashboardAdmin.fxml");
                } else if ("PLAYER".equalsIgnoreCase(role)) {
                    Navigation.goTo(event, "dashboardInterface.fxml");
                } else {
                    showAlert("Login Error", "Unknown user role.");
                }

            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not connect to database or check login.");
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws Exception {
        Navigation.goTo(event, "e-sportify.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}