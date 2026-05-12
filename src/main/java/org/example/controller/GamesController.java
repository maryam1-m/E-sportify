package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.database.DatabaseConnection;
import org.example.model.UserGame;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class GamesController extends DashboardController {

    @FXML
    private TableView<UserGame> gamesTable;

    @FXML
    private TableColumn<UserGame, Integer> gameIdColumn;

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

    private final ObservableList<UserGame> gamesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        gameIdColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        developerColumn.setCellValueFactory(new PropertyValueFactory<>("developerName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        loadGames();
    }

    private void loadGames() {
        gamesList.clear();

        String sql = """
                SELECT g.game_id, g.title, g.price, g.release_date, g.developer_name, c.category_name
                FROM Game g
                LEFT JOIN GameCategory c ON g.category_id = c.category_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                gamesList.add(new UserGame(
                        resultSet.getInt("game_id"),
                        resultSet.getString("title"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getDate("release_date").toLocalDate(),
                        resultSet.getString("developer_name"),
                        resultSet.getString("category_name")
                ));
            }

            gamesTable.setItems(gamesList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}