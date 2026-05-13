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

public class EditPlayerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField rankPointsField;

    @FXML
    private TextField winsField;

    @FXML
    private TextField lossesField;

    @FXML
    private TextField totalMatchesField;

    @FXML
    private DatePicker joinDatePicker;

    private int playerId = -1;

    @FXML
    public void initialize() {
        playerId = AdminPlayerFormContext.getEditPlayerId();
        if (playerId <= 0) {
            alert(Alert.AlertType.ERROR, "Edit Player", "No player selected. Use Back to return to the list.");
            AdminPlayerFormContext.clearEditPlayerId();
            return;
        }

        String sql = """
                SELECT name, email, join_date, rank_points, wins, losses, total_matches
                FROM Player
                WHERE player_id = ?
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    alert(Alert.AlertType.ERROR, "Edit Player", "Player not found.");
                    AdminPlayerFormContext.clearEditPlayerId();
                    playerId = -1;
                    return;
                }
                if (nameField != null) {
                    nameField.setText(rs.getString("name"));
                }
                if (emailField != null) {
                    emailField.setText(rs.getString("email"));
                }
                if (rankPointsField != null) {
                    rankPointsField.setText(String.valueOf(rs.getInt("rank_points")));
                }
                if (winsField != null) {
                    winsField.setText(String.valueOf(rs.getInt("wins")));
                }
                if (lossesField != null) {
                    lossesField.setText(String.valueOf(rs.getInt("losses")));
                }
                if (totalMatchesField != null) {
                    totalMatchesField.setText(String.valueOf(rs.getInt("total_matches")));
                }
                if (joinDatePicker != null) {
                    java.sql.Date jd = rs.getDate("join_date");
                    joinDatePicker.setValue(jd != null ? jd.toLocalDate() : LocalDate.now());
                }
            }
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Could not load player: " + e.getMessage());
        }
    }

    @FXML
    public void savePlayer(ActionEvent event) {
        if (playerId <= 0) {
            return;
        }

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
        if (!AddPlayerController.looksLikeEmail(email)) {
            alert(Alert.AlertType.WARNING, "Validation", "Please enter a valid email address.");
            return;
        }
        if (joinDate == null) {
            alert(Alert.AlertType.WARNING, "Validation", "Join date is required.");
            return;
        }

        int rankPoints;
        int wins;
        int losses;
        int totalMatches;
        try {
            rankPoints = Integer.parseInt(trim(rankPointsField));
            wins = Integer.parseInt(trim(winsField));
            losses = Integer.parseInt(trim(lossesField));
            totalMatches = Integer.parseInt(trim(totalMatchesField));
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation", "Rank points, wins, losses, and total matches must be whole numbers.");
            return;
        }
        if (rankPoints < 0 || wins < 0 || losses < 0 || totalMatches < 0) {
            alert(Alert.AlertType.WARNING, "Validation", "Numeric fields cannot be negative.");
            return;
        }

        try (Connection c = DatabaseConnection.getConnection()) {
            if (AddPlayerController.emailExists(c, email, playerId)) {
                alert(Alert.AlertType.WARNING, "Validation", "This email is already used by another player.");
                return;
            }

            String sql = """
                    UPDATE Player
                    SET name = ?, email = ?, join_date = ?, rank_points = ?, wins = ?, losses = ?, total_matches = ?
                    WHERE player_id = ?
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setDate(3, Date.valueOf(joinDate));
                ps.setInt(4, rankPoints);
                ps.setInt(5, wins);
                ps.setInt(6, losses);
                ps.setInt(7, totalMatches);
                ps.setInt(8, playerId);
                int n = ps.executeUpdate();
                if (n == 0) {
                    alert(Alert.AlertType.WARNING, "Update", "No rows were updated.");
                    return;
                }
            }
            alert(Alert.AlertType.INFORMATION, "Success", "Player updated successfully.");
            AdminPlayerFormContext.clearEditPlayerId();
            Navigation.goTo(event, "playersadmin.fxml");
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Could not update player: " + e.getMessage());
        }
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
        AdminPlayerFormContext.clearEditPlayerId();
        Navigation.goTo(event, "playersadmin.fxml");
    }
}
