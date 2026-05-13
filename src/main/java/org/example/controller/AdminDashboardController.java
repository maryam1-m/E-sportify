package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboardController {

    @FXML
    private Label playersCountLabel;

    @FXML
    private Label revenueLabel;

    @FXML
    private Label tournamentsCountLabel;

    @FXML
    private Label matchesCountLabel;

    @FXML
    public void initialize() {
        refreshDashboard(null);
    }

    public void goToDashboard(ActionEvent event) throws Exception {
        Navigation.goTo(event, "DashboardAdmin.fxml");
    }

    public void goToPlayers(ActionEvent event) throws Exception {
        Navigation.goTo(event, "playersadmin.fxml");
    }

    public void goToTeams(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Teamsadmin.fxml");
    }

    public void goToTournaments(ActionEvent event) throws Exception {
        Navigation.goTo(event, "TournmentAdmin.fxml");
    }

    public void goToMatches(ActionEvent event) throws Exception {
        Navigation.goTo(event, "MatchesAdmin.fxml");
    }

    public void goToGames(ActionEvent event) throws Exception {
        Navigation.goTo(event, "GamesAdmin.fxml");
    }

    public void goToSeasons(ActionEvent event) throws Exception {
        Navigation.goTo(event, "SeasonsAdmin.fxml");
    }

    public void logout(ActionEvent event) throws Exception {
        org.example.session.UserSession.clearSession();
        Navigation.goTo(event, "signinFrame.fxml");
    }

    public void refreshDashboard(ActionEvent event) {
        setIntLabel(playersCountLabel, "SELECT COUNT(*) AS total FROM Player");
        setIntLabel(tournamentsCountLabel, "SELECT COUNT(*) AS total FROM Tournament");
        setIntLabel(matchesCountLabel, "SELECT COUNT(*) AS total FROM MatchGame");
        setBigLabel(revenueLabel, "SELECT COALESCE(SUM(amount),0) AS total FROM Purchase WHERE status = 'Completed'");
    }

    private static void setIntLabel(Label label, String sql) {
        if (label == null) {
            return;
        }
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                label.setText(String.valueOf(rs.getInt("total")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            label.setText("-");
        }
    }

    private static void setBigLabel(Label label, String sql) {
        if (label == null) {
            return;
        }
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                label.setText(rs.getBigDecimal("total").toPlainString() + " USD");
            }
        } catch (Exception e) {
            e.printStackTrace();
            label.setText("-");
        }
    }
}
