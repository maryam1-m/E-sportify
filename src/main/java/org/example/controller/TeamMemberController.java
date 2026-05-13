package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;
import org.example.session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TeamMemberController extends DashboardController {

    @FXML private TextField searchMemberField;

    @FXML private Label totalMembersLabel;
    @FXML private Label selectedTeamLabel;
    @FXML private Label selectedMemberNameLabel;
    @FXML private Label selectedMemberRoleLabel;
    @FXML private Label selectedMemberGameLabel;
    @FXML private Label selectedMemberStatusLabel;
    @FXML private Label selectedMemberMatchesLabel;
    @FXML private Label selectedMemberWinsLabel;
    @FXML private Label selectedMemberRatingLabel;

    @FXML private Label member1NameLabel;
    @FXML private Label member1TeamLabel;
    @FXML private Label member1GameLabel;
    @FXML private Label member1RoleLabel;
    @FXML private Label member1StatusLabel;

    @FXML private Label member2NameLabel;
    @FXML private Label member2TeamLabel;
    @FXML private Label member2GameLabel;
    @FXML private Label member2RoleLabel;
    @FXML private Label member2StatusLabel;

    @FXML private Label member3NameLabel;
    @FXML private Label member3TeamLabel;
    @FXML private Label member3GameLabel;
    @FXML private Label member3RoleLabel;
    @FXML private Label member3StatusLabel;

    @FXML private Label member4NameLabel;
    @FXML private Label member4TeamLabel;
    @FXML private Label member4GameLabel;
    @FXML private Label member4RoleLabel;
    @FXML private Label member4StatusLabel;

    private final List<MemberRow> memberRows = new ArrayList<>();
    private Integer displayedTeamId;

    @FXML
    public void initialize() {
        loadMembers(null);
    }

    private void loadMembers(String keyword) {
        memberRows.clear();
        List<MemberRow> members = memberRows;

        String sql = """
                SELECT
                    p.player_id,
                    p.name AS player_name,
                    COALESCE(MAX(t.team_name), 'No Team') AS team_name,
                    MAX(t.team_id) AS team_id,
                    COALESCE(g.title, 'No Game') AS game_title,
                    COALESCE(p.total_matches, 0) AS total_matches,
                    COALESCE(p.wins, 0) AS wins,
                    COALESCE(p.losses, 0) AS losses
                FROM Player p
                LEFT JOIN TeamMembership tm ON p.player_id = tm.player_id
                LEFT JOIN Team t ON tm.team_id = t.team_id
                LEFT JOIN TournamentParticipation tp ON t.team_id = tp.team_id
                LEFT JOIN Tournament tr ON tp.tournament_id = tr.tournament_id
                LEFT JOIN Game g ON tr.game_id = g.game_id
                WHERE (? IS NULL OR p.name LIKE ? OR COALESCE(t.team_name, '') LIKE ? OR g.title LIKE ?)
                GROUP BY p.player_id, p.name, g.title, p.total_matches, p.wins, p.losses
                ORDER BY p.rank_points DESC
                LIMIT 4
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (keyword == null || keyword.trim().isEmpty()) {
                statement.setString(1, null);
                statement.setString(2, null);
                statement.setString(3, null);
                statement.setString(4, null);
            } else {
                String like = "%" + keyword.trim() + "%";
                statement.setString(1, keyword.trim());
                statement.setString(2, like);
                statement.setString(3, like);
                statement.setString(4, like);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int totalMatches = resultSet.getInt("total_matches");
                    int wins = resultSet.getInt("wins");
                    int losses = resultSet.getInt("losses");

                    Integer teamId = resultSet.getObject("team_id") == null
                            ? null
                            : resultSet.getInt("team_id");
                    members.add(new MemberRow(
                            resultSet.getString("player_name"),
                            resultSet.getString("team_name"),
                            teamId,
                            resultSet.getString("game_title"),
                            "Player",
                            "Active",
                            totalMatches,
                            wins,
                            losses,
                            calculateRating(totalMatches, wins)
                    ));
                }
            }

            setText(totalMembersLabel, String.valueOf(countMembers()));

            if (!members.isEmpty()) {
                fillSelectedMember(members.get(0));
            } else {
                clearSelectedMember();
            }

            fillMember(0, members, member1NameLabel, member1TeamLabel, member1GameLabel, member1RoleLabel, member1StatusLabel);
            fillMember(1, members, member2NameLabel, member2TeamLabel, member2GameLabel, member2RoleLabel, member2StatusLabel);
            fillMember(2, members, member3NameLabel, member3TeamLabel, member3GameLabel, member3RoleLabel, member3StatusLabel);
            fillMember(3, members, member4NameLabel, member4TeamLabel, member4GameLabel, member4RoleLabel, member4StatusLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countMembers() {
        String sql = "SELECT COUNT(*) AS total FROM TeamMembership";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String calculateRating(int totalMatches, int wins) {
        if (totalMatches <= 0) {
            return "0.0";
        }

        double ratio = (double) wins / totalMatches;
        double rating = 1.0 + (ratio * 4.0);

        return String.format("%.1f", rating);
    }

    private void fillSelectedMember(MemberRow member) {
        displayedTeamId = member.teamId;
        setText(selectedTeamLabel, member.team);
        setText(selectedMemberNameLabel, "Name: " + member.name);
        setText(selectedMemberRoleLabel, "Role: " + member.role);
        setText(selectedMemberGameLabel, "Game: " + member.game);
        setText(selectedMemberStatusLabel, "Status: " + member.status);
        setText(selectedMemberMatchesLabel, String.valueOf(member.matches));
        setText(selectedMemberWinsLabel, String.valueOf(member.wins));
        setText(selectedMemberRatingLabel, member.rating);
    }

    private void clearSelectedMember() {
        displayedTeamId = null;
        setText(selectedTeamLabel, "No Team");
        setText(selectedMemberNameLabel, "Name: -");
        setText(selectedMemberRoleLabel, "Role: -");
        setText(selectedMemberGameLabel, "Game: -");
        setText(selectedMemberStatusLabel, "Status: -");
        setText(selectedMemberMatchesLabel, "0");
        setText(selectedMemberWinsLabel, "0");
        setText(selectedMemberRatingLabel, "0.0");
    }

    private void fillMember(
            int index,
            List<MemberRow> members,
            Label nameLabel,
            Label teamLabel,
            Label gameLabel,
            Label roleLabel,
            Label statusLabel
    ) {
        if (index < members.size()) {
            MemberRow member = members.get(index);

            setText(nameLabel, member.name);
            setText(teamLabel, member.team);
            setText(gameLabel, member.game);
            setText(roleLabel, member.role);
            setText(statusLabel, member.status);
        } else {
            setText(nameLabel, "-");
            setText(teamLabel, "-");
            setText(gameLabel, "-");
            setText(roleLabel, "-");
            setText(statusLabel, "-");
        }
    }

    @FXML
    public void searchMember(ActionEvent event) {
        String keyword = searchMemberField == null ? "" : searchMemberField.getText();
        loadMembers(keyword);
    }

    @FXML
    public void viewMemberSlot1(ActionEvent event) {
        showMemberDetails(0);
    }

    @FXML
    public void viewMemberSlot2(ActionEvent event) {
        showMemberDetails(1);
    }

    @FXML
    public void viewMemberSlot3(ActionEvent event) {
        showMemberDetails(2);
    }

    @FXML
    public void viewMemberSlot4(ActionEvent event) {
        showMemberDetails(3);
    }

    private void showMemberDetails(int index) {
        if (index < 0 || index >= memberRows.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No member in this slot.").showAndWait();
            return;
        }
        MemberRow m = memberRows.get(index);
        fillSelectedMember(m);
        String body = "Name: " + m.name
                + "\nTeam: " + m.team
                + "\nGame: " + m.game
                + "\nRole: " + m.role
                + "\nStatus: " + m.status
                + "\nMatches: " + m.matches
                + "\nWins: " + m.wins
                + "\nLosses: " + m.losses
                + "\nRating: " + m.rating;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Member details");
        alert.setHeaderText(m.name);
        alert.setContentText(body);
        alert.showAndWait();
    }

    @FXML
    public void joinTeam(ActionEvent event) {
        int playerId = UserSession.getPlayerId();
        if (playerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please sign in with a player account to join a team.").showAndWait();
            return;
        }
        if (displayedTeamId == null || displayedTeamId <= 0) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No team is selected. Use View on a row for a member who belongs to the team you want to join.")
                    .showAndWait();
            return;
        }
        int teamId = displayedTeamId;

        String existsSql = "SELECT 1 FROM TeamMembership WHERE player_id = ? AND team_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(existsSql)) {
            check.setInt(1, playerId);
            check.setInt(2, teamId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    new Alert(Alert.AlertType.INFORMATION, "You are already in this team.").showAndWait();
                    return;
                }
            }

            String insertSql = """
                    INSERT INTO TeamMembership (player_id, team_id, join_date)
                    VALUES (?, ?, CURDATE())
                    """;
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, playerId);
                insert.setInt(2, teamId);
                insert.executeUpdate();
            }
            new Alert(Alert.AlertType.INFORMATION, "You joined the team successfully.").showAndWait();
            loadMembers(searchMemberField == null ? null : searchMemberField.getText());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not join team: " + e.getMessage()).showAndWait();
        }
    }

    private void setText(Label label, String value) {
        if (label != null) {
            label.setText(value);
        }
    }

    private static class MemberRow {
        String name;
        String team;
        Integer teamId;
        String game;
        String role;
        String status;
        int matches;
        int wins;
        int losses;
        String rating;

        MemberRow(
                String name,
                String team,
                Integer teamId,
                String game,
                String role,
                String status,
                int matches,
                int wins,
                int losses,
                String rating
        ) {
            this.name = name;
            this.team = team;
            this.teamId = teamId;
            this.game = game;
            this.role = role;
            this.status = status;
            this.matches = matches;
            this.wins = wins;
            this.losses = losses;
            this.rating = rating;
        }
    }
}