package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AddPlayerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField rankPointsField;

    @FXML
    private DatePicker joinDatePicker;

    @FXML
    public void initialize() {
        if (joinDatePicker != null) {
            joinDatePicker.setValue(LocalDate.now());
        }
        if (rankPointsField != null) {
            rankPointsField.setText("0");
        }
    }

    @FXML
    public void savePlayer(ActionEvent event) {
        String name = trim(nameField);
        String email = trim(emailField);
        LocalDate joinDate = joinDatePicker == null ? null : joinDatePicker.getValue();

        if (name.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation", "Player name is required.");
            return;
        }
        if (email.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation", "Email is required.");
            return;
        }
        if (!looksLikeEmail(email)) {
            alert(Alert.AlertType.WARNING, "Validation", "Please enter a valid email address.");
            return;
        }
        if (joinDate == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Join date is required.");
            return;
        }

        int rankPoints;
        try {
            String rp = trim(rankPointsField);
            if (rp.isEmpty()) {
                rp = "0";
            }
            rankPoints = Integer.parseInt(rp);
            if (rankPoints < 0) {
                alert(Alert.AlertType.WARNING, "Validation", "Rank points cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation", "Rank points must be a whole number.");
            return;
        }

        try (Connection c = DatabaseConnection.getConnection()) {
            if (emailExists(c, email, -1)) {
                alert(Alert.AlertType.WARNING, "Validation", "This email is already used by another player.");
                return;
            }

            String sql = """
                    INSERT INTO Player (name, email, join_date, rank_points, wins, losses, total_matches)
                    VALUES (?, ?, ?, ?, 0, 0, 0)
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setDate(3, Date.valueOf(joinDate));
                ps.setInt(4, rankPoints);
                ps.executeUpdate();
            }
            alert(Alert.AlertType.INFORMATION, "Success", "Player added successfully.");
            Navigation.goTo(event, "playersadmin.fxml");
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Could not add player: " + e.getMessage());
        }
    }

    static boolean emailExists(Connection c, String email, int excludePlayerId) throws Exception {
        String sql = excludePlayerId > 0
                ? "SELECT 1 FROM Player WHERE email = ? AND player_id <> ? LIMIT 1"
                : "SELECT 1 FROM Player WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            if (excludePlayerId > 0) {
                ps.setInt(2, excludePlayerId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean looksLikeEmail(String email) {
        return email.contains("@") && email.indexOf('@') < email.length() - 1;
    }

    private static String trim(TextField field) {
        if (field == null || field.getText() == null) {
            return "";
        }
        return field.getText().trim();
    }

    private static void alert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    @FXML
    public void cancel(ActionEvent event) throws Exception {
        Navigation.goTo(event, "playersadmin.fxml");
    }
}
