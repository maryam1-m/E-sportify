package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;
import org.example.model.UserGame;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AdminGamesController extends AdminDashboardController {

    @FXML
    private Label totalGamesLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TextField gameIdField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField priceField;

    @FXML
    private DatePicker releaseDatePicker;

    @FXML
    private TextField developerField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TableView<UserGame> gamesTable;

    @FXML
    private TableColumn<UserGame, Integer> idColumn;

    @FXML
    private TableColumn<UserGame, String> titleColumn;

    @FXML
    private TableColumn<UserGame, BigDecimal> priceColumn;

    @FXML
    private TableColumn<UserGame, LocalDate> releaseDateColumn;

    @FXML
    private TableColumn<UserGame, String> developerColumn;

    @FXML
    private TableColumn<UserGame, String> categoryColumn;

    @FXML
    private Label messageLabel;

    private final ObservableList<UserGame> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        }
        if (titleColumn != null) {
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        }
        if (priceColumn != null) {
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        }
        if (releaseDateColumn != null) {
            releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        }
        if (developerColumn != null) {
            developerColumn.setCellValueFactory(new PropertyValueFactory<>("developerName"));
        }
        if (categoryColumn != null) {
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        }
        reloadFromDb();
    }

    private void reloadFromDb() {
        rows.clear();
        String sql = """
                SELECT g.game_id, g.title, g.price, g.release_date, g.developer_name, c.category_name
                FROM Game g
                LEFT JOIN GameCategory c ON g.category_id = c.category_id
                ORDER BY g.game_id
                """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate rd = rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null;
                rows.add(new UserGame(
                        rs.getInt("game_id"),
                        rs.getString("title"),
                        rs.getBigDecimal("price"),
                        rd,
                        rs.getString("developer_name"),
                        rs.getString("category_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gamesTable != null) {
            gamesTable.setItems(rows);
        }
        if (totalGamesLabel != null) {
            totalGamesLabel.setText(String.valueOf(rows.size()));
        }
    }

    public void addGame(ActionEvent event) {
        reloadFromDb();
    }

    public void updateGame(ActionEvent event) {
        reloadFromDb();
    }

    public void deleteGame(ActionEvent event) {
        reloadFromDb();
    }

    public void clearFields(ActionEvent event) {
        if (gameIdField != null) {
            gameIdField.clear();
        }
        if (titleField != null) {
            titleField.clear();
        }
        if (priceField != null) {
            priceField.clear();
        }
        if (releaseDatePicker != null) {
            releaseDatePicker.setValue(null);
        }
        if (developerField != null) {
            developerField.clear();
        }
        if (categoryComboBox != null) {
            categoryComboBox.getSelectionModel().clearSelection();
        }
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    public void searchGames(ActionEvent event) {
        reloadFromDb();
    }

    public void loadGames(ActionEvent event) {
        reloadFromDb();
    }
}
