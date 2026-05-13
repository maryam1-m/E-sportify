package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;
import org.example.model.UserTournament;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AdminTournamentsController extends AdminDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private TextField tournamentNameField;

    @FXML
    private TextField gameIdField;

    @FXML
    private TextField seasonIdField;

    @FXML
    private TextField adminIdField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField prizePoolField;

    @FXML
    private TableView<UserTournament> matchesTable;

    @FXML
    private TableColumn<UserTournament, Integer> matchIdColumn;

    @FXML
    private TableColumn<UserTournament, String> tournamentColumn;

    @FXML
    private TableColumn<UserTournament, LocalDate> dateColumn;

    @FXML
    private TableColumn<UserTournament, String> teamOneColumn;

    @FXML
    private TableColumn<UserTournament, String> teamTwoColumn;

    @FXML
    private TableColumn<UserTournament, BigDecimal> resultColumn;

    private final ObservableList<UserTournament> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (matchIdColumn != null) {
            matchIdColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTournamentId()));
        }
        if (tournamentColumn != null) {
            tournamentColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getName()));
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStartDate()));
        }
        if (teamOneColumn != null) {
            teamOneColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getGameTitle()));
        }
        if (teamTwoColumn != null) {
            teamTwoColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSeasonName()));
        }
        if (resultColumn != null) {
            resultColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrizePool()));
        }
        refreshTable(null);
    }

    private void reload() {
        rows.clear();
        String sql = """
                SELECT t.tournament_id, t.name, t.start_date, t.end_date, t.prize_pool,
                       g.title AS game_title,
                       s.season_name
                FROM Tournament t
                JOIN Game g ON t.game_id = g.game_id
                JOIN Season s ON t.season_id = s.season_id
                ORDER BY t.tournament_id
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new UserTournament(
                        rs.getInt("tournament_id"),
                        rs.getString("name"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBigDecimal("prize_pool"),
                        rs.getString("game_title"),
                        rs.getString("season_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (matchesTable != null) {
            matchesTable.setItems(rows);
        }
    }

    public void addTournament(ActionEvent event) {
        reload();
    }

    public void updateTournament(ActionEvent event) {
        reload();
    }

    public void deleteTournament(ActionEvent event) {
        reload();
    }

    public void clearFields(ActionEvent event) {
        if (tournamentNameField != null) {
            tournamentNameField.clear();
        }
        if (gameIdField != null) {
            gameIdField.clear();
        }
        if (seasonIdField != null) {
            seasonIdField.clear();
        }
        if (adminIdField != null) {
            adminIdField.clear();
        }
        if (startDatePicker != null) {
            startDatePicker.setValue(null);
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(null);
        }
        if (prizePoolField != null) {
            prizePoolField.clear();
        }
    }

    public void searchTournament(ActionEvent event) {
        reload();
    }

    public void refreshTable(ActionEvent event) {
        reload();
    }
}
