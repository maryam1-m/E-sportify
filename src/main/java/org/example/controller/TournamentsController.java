package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;
import org.example.model.UserTournament;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class TournamentsController extends DashboardController {

    @FXML
    private TableView<UserTournament> tournamentsTable;

    @FXML
    private TableColumn<UserTournament, Integer> tournamentIdColumn;

    @FXML
    private TableColumn<UserTournament, String> tournamentNameColumn;

    @FXML
    private TableColumn<UserTournament, String> gameTitleColumn;

    @FXML
    private TableColumn<UserTournament, String> seasonNameColumn;

    @FXML
    private TableColumn<UserTournament, LocalDate> startDateColumn;

    @FXML
    private TableColumn<UserTournament, LocalDate> endDateColumn;

    @FXML
    private TableColumn<UserTournament, BigDecimal> prizePoolColumn;

    private final ObservableList<UserTournament> tournamentsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tournamentIdColumn.setCellValueFactory(new PropertyValueFactory<>("tournamentId"));
        tournamentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        seasonNameColumn.setCellValueFactory(new PropertyValueFactory<>("seasonName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        prizePoolColumn.setCellValueFactory(new PropertyValueFactory<>("prizePool"));

        loadTournaments();
    }

    private void loadTournaments() {
        tournamentsList.clear();

        String sql = """
                SELECT t.tournament_id, t.name, t.start_date, t.end_date, t.prize_pool,
                       g.title AS game_title,
                       s.season_name
                FROM Tournament t
                JOIN Game g ON t.game_id = g.game_id
                JOIN Season s ON t.season_id = s.season_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tournamentsList.add(new UserTournament(
                        resultSet.getInt("tournament_id"),
                        resultSet.getString("name"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBigDecimal("prize_pool"),
                        resultSet.getString("game_title"),
                        resultSet.getString("season_name")
                ));
            }

            tournamentsTable.setItems(tournamentsList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}