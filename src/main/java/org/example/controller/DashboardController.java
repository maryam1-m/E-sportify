package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.database.DatabaseConnection;
import org.example.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label rankLabel;

    @FXML
    private Label rankPointsLabel;

    @FXML
    private Label teamLabel;

    @FXML
    private Label achievementsLabel;

    @FXML
    private Label matchesLabel;

    @FXML
    private Label winsLabel;

    @FXML
    private Label lossesLabel;

    @FXML
    public void initialize() {
        loadUserDashboard();
    }

    private void loadUserDashboard() {
        int playerId = UserSession.getPlayerId();

        if (playerId == 0) {
            return;
        }

        loadPlayerInfo(playerId);
        loadPlayerTeam(playerId);
        loadAchievementsCount(playerId);
        loadSeasonRank(playerId);
    }

    private void loadPlayerInfo(int playerId) {
        String sql = """
                SELECT name, rank_points, wins, losses, total_matches
                FROM Player
                WHERE player_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int rankPoints = resultSet.getInt("rank_points");
                    int wins = resultSet.getInt("wins");
                    int losses = resultSet.getInt("losses");
                    int totalMatches = resultSet.getInt("total_matches");

                    if (welcomeLabel != null) {
                        welcomeLabel.setText("Welcome back, " + name);
                    }

                    if (rankPointsLabel != null) {
                        rankPointsLabel.setText(String.valueOf(rankPoints));
                    }

                    if (winsLabel != null) {
                        winsLabel.setText(String.valueOf(wins));
                    }

                    if (lossesLabel != null) {
                        lossesLabel.setText(String.valueOf(losses));
                    }

                    if (matchesLabel != null) {
                        matchesLabel.setText(String.valueOf(totalMatches));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerTeam(int playerId) {
        String sql = """
                SELECT t.team_name
                FROM TeamMembership tm
                JOIN Team t ON tm.team_id = t.team_id
                WHERE tm.player_id = ?
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (teamLabel != null) {
                        teamLabel.setText(resultSet.getString("team_name"));
                    }
                } else {
                    if (teamLabel != null) {
                        teamLabel.setText("No Team");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAchievementsCount(int playerId) {
        String sql = """
                SELECT COUNT(*) AS achievement_count
                FROM PlayerAchievement
                WHERE player_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (achievementsLabel != null) {
                        achievementsLabel.setText(String.valueOf(resultSet.getInt("achievement_count")));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSeasonRank(int playerId) {
        String sql = """
                SELECT rank_position
                FROM Ranking
                WHERE player_id = ?
                ORDER BY season_id DESC
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (rankLabel != null) {
                        rankLabel.setText("#" + resultSet.getInt("rank_position"));
                    }
                } else {
                    if (rankLabel != null) {
                        rankLabel.setText("#-");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToDashboard(ActionEvent event) throws Exception {
        Navigation.goTo(event, "dashboardInterface.fxml");
    }

    public void goToPlayers(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Players.fxml");
    }

    public void goToTeams(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Teams.fxml");
    }

    public void goToTournaments(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Tourment.fxml");
    }

    public void goToMatches(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Matches.fxml");
    }

    public void goToGames(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Games.fxml");
    }

    public void goToSeasons(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Seasons.fxml");
    }

    public void logout(ActionEvent event) throws Exception {
        UserSession.clearSession();
        Navigation.goTo(event, "signinFrame.fxml");
    }
}