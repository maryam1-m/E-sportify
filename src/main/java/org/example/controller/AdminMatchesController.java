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
import org.example.model.UserMatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AdminMatchesController extends AdminDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private TextField tournamentIdField;

    @FXML
    private DatePicker matchDatePicker;

    @FXML
    private TextField teamOneIdField;

    @FXML
    private TextField teamTwoIdField;

    @FXML
    private TextField teamOneRankField;

    @FXML
    private TextField teamTwoRankField;

    @FXML
    private TableView<UserMatch> matchesTable;

    @FXML
    private TableColumn<UserMatch, Integer> matchIdColumn;

    @FXML
    private TableColumn<UserMatch, String> tournamentColumn;

    @FXML
    private TableColumn<UserMatch, LocalDate> dateColumn;

    @FXML
    private TableColumn<UserMatch, String> teamOneColumn;

    @FXML
    private TableColumn<UserMatch, String> teamTwoColumn;

    @FXML
    private TableColumn<UserMatch, String> resultColumn;

    private final ObservableList<UserMatch> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (matchIdColumn != null) {
            matchIdColumn.setCellValueFactory(new PropertyValueFactory<>("matchId"));
        }
        if (tournamentColumn != null) {
            tournamentColumn.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        }
        if (teamOneColumn != null) {
            teamOneColumn.setCellValueFactory(new PropertyValueFactory<>("teams"));
        }
        if (teamTwoColumn != null) {
            teamTwoColumn.setCellValueFactory(new PropertyValueFactory<>("teams"));
        }
        if (resultColumn != null) {
            resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        }
        refreshTable(null);
    }

    @FXML
    public void searchMatch(ActionEvent event) {
        refreshTable(null);
    }

    @FXML
    public void addMatch(ActionEvent event) {
        /* Placeholder: full CRUD later */
    }

    @FXML
    public void updateMatch(ActionEvent event) {
        /* Placeholder */
    }

    @FXML
    public void deleteMatch(ActionEvent event) {
        /* Placeholder */
    }

    @FXML
    public void clearFields(ActionEvent event) {
        if (tournamentIdField != null) {
            tournamentIdField.clear();
        }
        if (matchDatePicker != null) {
            matchDatePicker.setValue(null);
        }
        if (teamOneIdField != null) {
            teamOneIdField.clear();
        }
        if (teamTwoIdField != null) {
            teamTwoIdField.clear();
        }
        if (teamOneRankField != null) {
            teamOneRankField.clear();
        }
        if (teamTwoRankField != null) {
            teamTwoRankField.clear();
        }
    }

    @FXML
    public void refreshTable(ActionEvent event) {
        rows.clear();

        String sql = """
                SELECT mg.match_id,
                       COALESCE(tr.name, '') AS tournament_name,
                       mg.match_date,
                       GROUP_CONCAT(t.team_name ORDER BY mp.final_rank SEPARATOR ' vs ') AS teams,
                       GROUP_CONCAT(mp.final_rank ORDER BY mp.final_rank SEPARATOR ' - ') AS result_text
                FROM MatchGame mg
                LEFT JOIN Tournament tr ON mg.tournament_id = tr.tournament_id
                LEFT JOIN MatchParticipant mp ON mg.match_id = mp.match_id
                LEFT JOIN Team t ON mp.team_id = t.team_id
                GROUP BY mg.match_id, tr.name, mg.match_date
                ORDER BY mg.match_date DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String teams = rs.getString("teams");
                if (teams == null) {
                    teams = "";
                }
                String result = rs.getString("result_text");
                if (result == null) {
                    result = "-";
                }
                rows.add(new UserMatch(
                        rs.getInt("match_id"),
                        rs.getString("tournament_name"),
                        rs.getDate("match_date").toLocalDate(),
                        teams,
                        result
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (matchesTable != null) {
            matchesTable.setItems(rows);
        }
    }
}
