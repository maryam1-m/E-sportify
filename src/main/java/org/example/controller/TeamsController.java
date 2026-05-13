package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class TeamsController extends AdminDashboardController {

    @FXML
    private TextField teamNameField;

    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private TableView<TeamAdminRow> teamsTable;

    @FXML
    private TableColumn<TeamAdminRow, Integer> teamIdColumn;

    @FXML
    private TableColumn<TeamAdminRow, String> teamNameColumn;

    @FXML
    private TableColumn<TeamAdminRow, LocalDate> creationDateColumn;

    @FXML
    private TableColumn<TeamAdminRow, Integer> playersCountColumn;

    private final ObservableList<TeamAdminRow> teamRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (teamIdColumn != null) {
            teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("teamId"));
        }
        if (teamNameColumn != null) {
            teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        }
        if (creationDateColumn != null) {
            creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        }
        if (playersCountColumn != null) {
            playersCountColumn.setCellValueFactory(new PropertyValueFactory<>("playersCount"));
        }
        reloadTeamsTable();
    }

    private void reloadTeamsTable() {
        teamRows.clear();
        String sql = """
                SELECT t.team_id, t.team_name, t.creation_date,
                       COUNT(tm.membership_id) AS players_count
                FROM Team t
                LEFT JOIN TeamMembership tm ON t.team_id = tm.team_id
                GROUP BY t.team_id, t.team_name, t.creation_date
                ORDER BY t.team_name
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                LocalDate cd = rs.getDate("creation_date") != null
                        ? rs.getDate("creation_date").toLocalDate()
                        : null;
                teamRows.add(new TeamAdminRow(
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        cd,
                        rs.getInt("players_count")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (teamsTable != null) {
            teamsTable.setItems(teamRows);
        }
    }

    public void addTeam(ActionEvent event) {
        reloadTeamsTable();
    }

    public void updateTeam(ActionEvent event) {
        reloadTeamsTable();
    }

    public void deleteTeam(ActionEvent event) {
        reloadTeamsTable();
    }

    public void clearFields(ActionEvent event) {
        if (teamNameField != null) {
            teamNameField.clear();
        }
        if (creationDatePicker != null) {
            creationDatePicker.setValue(null);
        }
    }

    public static class TeamAdminRow {
        private final int teamId;
        private final String teamName;
        private final LocalDate creationDate;
        private final int playersCount;

        public TeamAdminRow(int teamId, String teamName, LocalDate creationDate, int playersCount) {
            this.teamId = teamId;
            this.teamName = teamName;
            this.creationDate = creationDate;
            this.playersCount = playersCount;
        }

        public int getTeamId() {
            return teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public LocalDate getCreationDate() {
            return creationDate;
        }

        public int getPlayersCount() {
            return playersCount;
        }
    }
}
