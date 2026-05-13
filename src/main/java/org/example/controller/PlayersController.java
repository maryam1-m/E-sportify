package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlayersController extends DashboardController {

    @FXML
    private Label topPlayerNameLabel;
    @FXML
    private Label topPlayerInfoLabel;
    @FXML
    private Label topPlayerRankLabel;

    @FXML
    private Label featuredPlayerNameLabel;
    @FXML
    private Label featuredPlayerEmailLabel;
    @FXML
    private Label featuredPlayerJoinDateLabel;
    @FXML
    private Label featuredPlayerTeamLabel;
    @FXML
    private Label featuredPlayerAchievementLabel;

    @FXML
    private Label rank1Label;
    @FXML
    private Label player1NameLabel;
    @FXML
    private Label player1InfoLabel;

    @FXML
    private Label rank2Label;
    @FXML
    private Label player2NameLabel;
    @FXML
    private Label player2InfoLabel;

    @FXML
    private Label rank3Label;
    @FXML
    private Label player3NameLabel;
    @FXML
    private Label player3InfoLabel;

    @FXML
    private Label rank4Label;
    @FXML
    private Label player4NameLabel;
    @FXML
    private Label player4InfoLabel;

    private final List<PlayerRow> playerRows = new ArrayList<>();
    private boolean rankDescending = true;

    @FXML
    public void initialize() {
        loadPlayers();
    }

    private void loadPlayers() {
        playerRows.clear();
        String order = rankDescending ? "DESC" : "ASC";

        String sql = """
                SELECT
                    p.player_id,
                    p.name,
                    p.email,
                    p.join_date,
                    p.rank_points,
                    COALESCE(t.team_name, 'No Team') AS team_name,
                    COALESCE(a.title, 'No Achievement') AS achievement_title,
                    COALESCE(r.rank_position, 0) AS rank_position
                FROM Player p
                LEFT JOIN TeamMembership tm ON p.player_id = tm.player_id
                LEFT JOIN Team t ON tm.team_id = t.team_id
                LEFT JOIN PlayerAchievement pa ON p.player_id = pa.player_id
                LEFT JOIN Achievement a ON pa.achievement_id = a.achievement_id
                LEFT JOIN Ranking r ON p.player_id = r.player_id
                GROUP BY p.player_id, p.name, p.email, p.join_date, p.rank_points, t.team_name, a.title, r.rank_position
                ORDER BY p.rank_points %s
                LIMIT 4
                """.formatted(order);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                playerRows.add(new PlayerRow(
                        resultSet.getInt("player_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        String.valueOf(resultSet.getDate("join_date")),
                        resultSet.getInt("rank_points"),
                        resultSet.getString("team_name"),
                        resultSet.getString("achievement_title"),
                        resultSet.getInt("rank_position")
                ));
            }

            if (!playerRows.isEmpty()) {
                PlayerRow top = playerRows.get(0);

                setText(topPlayerNameLabel, top.name);
                setText(topPlayerInfoLabel, top.team + " | " + top.rankPoints + " pts | " + top.achievement);
                setText(topPlayerRankLabel, getRankText(top, 1));

                setText(featuredPlayerNameLabel, top.name);
                setText(featuredPlayerEmailLabel, "Email: " + top.email);
                setText(featuredPlayerJoinDateLabel, "Join Date: " + top.joinDate);
                setText(featuredPlayerTeamLabel, "Team: " + top.team);
                setText(featuredPlayerAchievementLabel, "Achievement: " + top.achievement);
            }

            fillPlayer(0, playerRows, rank1Label, player1NameLabel, player1InfoLabel);
            fillPlayer(1, playerRows, rank2Label, player2NameLabel, player2InfoLabel);
            fillPlayer(2, playerRows, rank3Label, player3NameLabel, player3InfoLabel);
            fillPlayer(3, playerRows, rank4Label, player4NameLabel, player4InfoLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openTopPlayer(ActionEvent event) {
        showPlayerDetails(0);
    }

    @FXML
    public void viewFeaturedPlayerDetails(ActionEvent event) {
        showPlayerDetails(0);
    }

    @FXML
    public void filterPlayers(ActionEvent event) {
        rankDescending = !rankDescending;
        loadPlayers();
        new Alert(Alert.AlertType.INFORMATION,
                "Players reordered by rank points (" + (rankDescending ? "high → low" : "low → high") + ").").showAndWait();
    }

    @FXML
    public void openPlayerRow1(ActionEvent event) {
        showPlayerDetails(0);
    }

    @FXML
    public void openPlayerRow2(ActionEvent event) {
        showPlayerDetails(1);
    }

    @FXML
    public void openPlayerRow3(ActionEvent event) {
        showPlayerDetails(2);
    }

    @FXML
    public void openPlayerRow4(ActionEvent event) {
        showPlayerDetails(3);
    }

    private void showPlayerDetails(int index) {
        if (index < 0 || index >= playerRows.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No player in this slot.").showAndWait();
            return;
        }
        PlayerRow p = playerRows.get(index);
        String body = "ID: " + p.playerId
                + "\nName: " + p.name
                + "\nEmail: " + p.email
                + "\nJoin date: " + p.joinDate
                + "\nRank points: " + p.rankPoints
                + "\nTeam: " + p.team
                + "\nAchievement: " + p.achievement
                + "\nSeason rank #: " + (p.rankPosition > 0 ? p.rankPosition : "—");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Player details");
        alert.setHeaderText(p.name);
        alert.setContentText(body);
        alert.showAndWait();
    }

    private void fillPlayer(int index, List<PlayerRow> players, Label rankLabel, Label nameLabel, Label infoLabel) {
        if (index < players.size()) {
            PlayerRow player = players.get(index);

            setText(rankLabel, getRankText(player, index + 1));
            setText(nameLabel, player.name);
            setText(infoLabel, player.team + " | " + player.rankPoints + " pts | " + player.achievement);
        } else {
            setText(rankLabel, "#-");
            setText(nameLabel, "-");
            setText(infoLabel, "- | - | -");
        }
    }

    private String getRankText(PlayerRow player, int fallbackRank) {
        if (player.rankPosition > 0) {
            return "#" + player.rankPosition;
        }

        return "#" + fallbackRank;
    }

    private void setText(Label label, String value) {
        if (label != null) {
            label.setText(value);
        }
    }

    private static class PlayerRow {
        int playerId;
        String name;
        String email;
        String joinDate;
        int rankPoints;
        String team;
        String achievement;
        int rankPosition;

        PlayerRow(
                int playerId,
                String name,
                String email,
                String joinDate,
                int rankPoints,
                String team,
                String achievement,
                int rankPosition
        ) {
            this.playerId = playerId;
            this.name = name;
            this.email = email;
            this.joinDate = joinDate;
            this.rankPoints = rankPoints;
            this.team = team;
            this.achievement = achievement;
            this.rankPosition = rankPosition;
        }
    }
}
