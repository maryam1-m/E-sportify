package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;

public class AdminPlayersController extends AdminDashboardController {

    @FXML
    private TextField searchPlayerField;

    @FXML
    private TableView<AdminPlayerRow> playersTable;

    @FXML
    private TableColumn<AdminPlayerRow, Integer> playerIdColumn;

    @FXML
    private TableColumn<AdminPlayerRow, String> playerNameColumn;

    @FXML
    private TableColumn<AdminPlayerRow, String> emailColumn;

    @FXML
    private TableColumn<AdminPlayerRow, LocalDate> joinDateColumn;

    @FXML
    private TableColumn<AdminPlayerRow, Integer> rankPointsColumn;

    private final ObservableList<AdminPlayerRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (playerIdColumn != null) {
            playerIdColumn.setCellValueFactory(new PropertyValueFactory<>("playerId"));
        }
        if (playerNameColumn != null) {
            playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (emailColumn != null) {
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        }
        if (joinDateColumn != null) {
            joinDateColumn.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
        }
        if (rankPointsColumn != null) {
            rankPointsColumn.setCellValueFactory(new PropertyValueFactory<>("rankPoints"));
        }
        reloadTable();
    }

    private String currentSearchKeyword() {
        return searchPlayerField == null ? "" : searchPlayerField.getText().trim();
    }

    private void reloadTable() {
        rows.clear();
        String keyword = currentSearchKeyword();
        boolean filter = !keyword.isEmpty();
        String sql = filter
                ? """
                SELECT player_id, name, email, join_date, rank_points
                FROM Player
                WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)
                ORDER BY rank_points DESC
                """
                : """
                SELECT player_id, name, email, join_date, rank_points
                FROM Player
                ORDER BY rank_points DESC
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (filter) {
                String like = "%" + keyword + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate jd = rs.getDate("join_date") != null ? rs.getDate("join_date").toLocalDate() : null;
                    rows.add(new AdminPlayerRow(
                            rs.getInt("player_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            jd,
                            rs.getInt("rank_points")
                    ));
                }
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load players: " + e.getMessage()).showAndWait();
        }
        if (playersTable != null) {
            playersTable.setItems(rows);
        }
    }

    @FXML
    public void searchPlayers(ActionEvent event) {
        reloadTable();
    }

    @FXML
    public void refreshPlayers(ActionEvent event) {
        if (searchPlayerField != null) {
            searchPlayerField.clear();
        }
        reloadTable();
    }

    @FXML
    public void openAddPlayer(ActionEvent event) throws Exception {
        AdminPlayerFormContext.clearEditPlayerId();
        Navigation.goTo(event, "AddPlayer.fxml");
    }

    @FXML
    public void openEditPlayer(ActionEvent event) throws Exception {
        AdminPlayerRow selected = playersTable == null ? null : playersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a player in the table, then choose Edit.").showAndWait();
            return;
        }
        AdminPlayerFormContext.setEditPlayerId(selected.getPlayerId());
        Navigation.goTo(event, "EditPlayer.fxml");
    }

    @FXML
    public void viewPlayerDetails(ActionEvent event) {
        AdminPlayerRow selected = playersTable == null ? null : playersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a player to view details.").showAndWait();
            return;
        }
        int id = selected.getPlayerId();
        String sql = """
                SELECT name, email, join_date, rank_points, wins, losses, total_matches
                FROM Player
                WHERE player_id = ?
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    new Alert(Alert.AlertType.INFORMATION, "Player not found.").showAndWait();
                    return;
                }
                String jd = rs.getDate("join_date") != null ? rs.getDate("join_date").toLocalDate().toString() : "—";
                String body = "ID: " + id
                        + "\nName: " + rs.getString("name")
                        + "\nEmail: " + rs.getString("email")
                        + "\nJoin date: " + jd
                        + "\nRank points: " + rs.getInt("rank_points")
                        + "\nWins: " + rs.getInt("wins")
                        + "\nLosses: " + rs.getInt("losses")
                        + "\nTotal matches: " + rs.getInt("total_matches");
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Player details");
                a.setHeaderText(rs.getString("name"));
                a.setContentText(body);
                a.showAndWait();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load details: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void deleteSelectedPlayer(ActionEvent event) {
        AdminPlayerRow selected = playersTable == null ? null : playersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a player to delete.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete player");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete player \"" + selected.getName() + "\" (ID " + selected.getPlayerId()
                + ")? Related memberships and stats links will be removed where possible.");
        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }
        int id = selected.getPlayerId();
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                deletePrepared(c, "DELETE FROM TeamMembership WHERE player_id = ?", id);
                deletePrepared(c, "DELETE FROM PlayerAchievement WHERE player_id = ?", id);
                deletePrepared(c, "DELETE FROM Ranking WHERE player_id = ?", id);
                deletePrepared(c, "DELETE FROM Purchase WHERE player_id = ?", id);
                try (PreparedStatement ps = c.prepareStatement("UPDATE AppUser SET player_id = NULL WHERE player_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM Player WHERE player_id = ?")) {
                    ps.setInt(1, id);
                    int n = ps.executeUpdate();
                    if (n == 0) {
                        c.rollback();
                        new Alert(Alert.AlertType.WARNING, "Player was not deleted (not found).").showAndWait();
                        return;
                    }
                }
                c.commit();
                new Alert(Alert.AlertType.INFORMATION, "Player deleted.").showAndWait();
                reloadTable();
            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not delete player: " + e.getMessage()).showAndWait();
        }
    }

    private static void deletePrepared(Connection c, String sql, int playerId) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        }
    }

    public static class AdminPlayerRow {
        private final int playerId;
        private final String name;
        private final String email;
        private final LocalDate joinDate;
        private final int rankPoints;

        public AdminPlayerRow(int playerId, String name, String email, LocalDate joinDate, int rankPoints) {
            this.playerId = playerId;
            this.name = name;
            this.email = email;
            this.joinDate = joinDate;
            this.rankPoints = rankPoints;
        }

        public int getPlayerId() {
            return playerId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public LocalDate getJoinDate() {
            return joinDate;
        }

        public int getRankPoints() {
            return rankPoints;
        }
    }
}
