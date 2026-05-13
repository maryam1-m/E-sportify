package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;
import org.example.model.UserTournament;
import org.example.session.UserSession;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TournamentsController extends DashboardController {

    @FXML
    private TextField searchTournamentField;

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

    private final List<UserTournament> allTournaments = new ArrayList<>();
    private final ObservableList<UserTournament> tournamentsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (tournamentIdColumn != null) {
            tournamentIdColumn.setCellValueFactory(new PropertyValueFactory<>("tournamentId"));
        }
        if (tournamentNameColumn != null) {
            tournamentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (gameTitleColumn != null) {
            gameTitleColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        }
        if (seasonNameColumn != null) {
            seasonNameColumn.setCellValueFactory(new PropertyValueFactory<>("seasonName"));
        }
        if (startDateColumn != null) {
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        }
        if (endDateColumn != null) {
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        }
        if (prizePoolColumn != null) {
            prizePoolColumn.setCellValueFactory(new PropertyValueFactory<>("prizePool"));
        }

        loadTournaments();
    }

    private void loadTournaments() {
        allTournaments.clear();

        String sql = """
                SELECT t.tournament_id, t.name, t.start_date, t.end_date, t.prize_pool,
                       g.title AS game_title,
                       s.season_name
                FROM Tournament t
                JOIN Game g ON t.game_id = g.game_id
                JOIN Season s ON t.season_id = s.season_id
                ORDER BY t.start_date DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                allTournaments.add(new UserTournament(
                        resultSet.getInt("tournament_id"),
                        resultSet.getString("name"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getBigDecimal("prize_pool"),
                        resultSet.getString("game_title"),
                        resultSet.getString("season_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        applySearch("");
    }

    private void applySearch(String raw) {
        String q = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        tournamentsList.clear();
        for (UserTournament t : allTournaments) {
            if (q.isEmpty()
                    || contains(t.getName(), q)
                    || contains(t.getGameTitle(), q)
                    || contains(t.getSeasonName(), q)) {
                tournamentsList.add(t);
            }
        }
        if (tournamentsTable != null) {
            tournamentsTable.setItems(tournamentsList);
        }
    }

    private static boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    @FXML
    public void searchTournament(ActionEvent event) {
        String keyword = searchTournamentField == null ? "" : searchTournamentField.getText();
        applySearch(keyword);
    }

    @FXML
    public void addTournament(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tournaments");
        alert.setHeaderText(null);
        alert.setContentText("Adding tournaments is available in the Admin panel.");
        alert.showAndWait();
    }

    @FXML
    public void viewTournamentRow1Details(ActionEvent event) {
        showTournamentAt(0);
    }

    @FXML
    public void viewTournamentRow2Details(ActionEvent event) {
        showTournamentAt(1);
    }

    @FXML
    public void viewTournamentRow3Details(ActionEvent event) {
        showTournamentAt(2);
    }

    private void showTournamentAt(int index) {
        if (index < 0 || index >= allTournaments.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No tournament data for this row.").showAndWait();
            return;
        }
        UserTournament t = allTournaments.get(index);
        String body = "ID: " + t.getTournamentId()
                + "\nName: " + t.getName()
                + "\nGame: " + t.getGameTitle()
                + "\nSeason: " + t.getSeasonName()
                + "\nStart: " + t.getStartDate()
                + "\nEnd: " + t.getEndDate()
                + "\nPrize pool: " + t.getPrizePool();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tournament details");
        alert.setHeaderText(t.getName());
        alert.setContentText(body);
        alert.showAndWait();
    }

    @FXML
    public void joinTournamentRow1(ActionEvent event) {
        joinTournamentAt(0);
    }

    @FXML
    public void joinTournamentRow2(ActionEvent event) {
        joinTournamentAt(1);
    }

    @FXML
    public void joinTournamentRow3(ActionEvent event) {
        joinTournamentAt(2);
    }

    private void joinTournamentAt(int index) {
        int playerId = UserSession.getPlayerId();
        if (playerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please sign in with a player account to join a tournament.").showAndWait();
            return;
        }
        if (index < 0 || index >= allTournaments.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No tournament data for this row.").showAndWait();
            return;
        }
        int tournamentId = allTournaments.get(index).getTournamentId();

        Integer teamId;
        try {
            teamId = findTeamIdForPlayer(playerId);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not look up your team: " + e.getMessage()).showAndWait();
            return;
        }
        if (teamId == null) {
            new Alert(Alert.AlertType.INFORMATION, "Join a team first before joining a tournament.").showAndWait();
            return;
        }

        String existsSql = "SELECT 1 FROM TournamentParticipation WHERE tournament_id = ? AND team_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(existsSql)) {
            check.setInt(1, tournamentId);
            check.setInt(2, teamId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    new Alert(Alert.AlertType.INFORMATION, "Your team already joined this tournament.").showAndWait();
                    return;
                }
            }

            String insertSql = "INSERT INTO TournamentParticipation (tournament_id, team_id) VALUES (?, ?)";
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, tournamentId);
                insert.setInt(2, teamId);
                insert.executeUpdate();
            }
            new Alert(Alert.AlertType.INFORMATION, "Your team joined the tournament successfully.").showAndWait();
            loadTournaments();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not join tournament: " + e.getMessage()).showAndWait();
        }
    }

    private Integer findTeamIdForPlayer(int playerId) throws Exception {
        String sql = "SELECT team_id FROM TeamMembership WHERE player_id = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("team_id");
                }
            }
        }
        return null;
    }
}
