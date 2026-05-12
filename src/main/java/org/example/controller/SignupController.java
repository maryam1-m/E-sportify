package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void handleSignup(ActionEvent event) {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Sign Up Error", "Please fill all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Sign Up Error", "Please enter a valid email.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Sign Up Error", "Passwords do not match.");
            return;
        }

        if (password.length() < 4) {
            showAlert(Alert.AlertType.WARNING, "Sign Up Error", "Password must be at least 4 characters.");
            return;
        }

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            if (emailExistsInAppUser(connection, email) || emailExistsInPlayer(connection, email)) {
                showAlert(Alert.AlertType.WARNING, "Sign Up Error", "This email is already registered.");
                connection.rollback();
                return;
            }

            int playerId = insertPlayer(connection, name, email);
            insertAppUser(connection, name, email, password, playerId);

            connection.commit();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully. Please sign in.");
            Navigation.goTo(event, "signInFrame.fxml");

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create account.");

        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (Exception closeError) {
                closeError.printStackTrace();
            }
        }
    }

    private boolean emailExistsInAppUser(Connection connection, String email) throws Exception {
        String sql = "SELECT user_id FROM AppUser WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean emailExistsInPlayer(Connection connection, String email) throws Exception {
        String sql = "SELECT player_id FROM Player WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int insertPlayer(Connection connection, String name, String email) throws Exception {
        String sql = """
                INSERT INTO Player (name, email, join_date, rank_points, wins, losses, total_matches)
                VALUES (?, ?, ?, 0, 0, 0, 0)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setDate(3, Date.valueOf(LocalDate.now()));

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted == 0) {
                throw new Exception("Player insert failed. No rows inserted.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new Exception("Could not get generated player ID.");
    }

    private void insertAppUser(Connection connection, String name, String email, String password, int playerId) throws Exception {
        String sql = """
                INSERT INTO AppUser (name, email, password, role, player_id, admin_id)
                VALUES (?, ?, ?, 'PLAYER', ?, NULL)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setInt(4, playerId);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted == 0) {
                throw new Exception("AppUser insert failed. No rows inserted.");
            }
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws Exception {
        Navigation.goTo(event, "e-sportify.fxml");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}