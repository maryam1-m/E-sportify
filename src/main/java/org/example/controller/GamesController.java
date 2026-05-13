package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.database.DatabaseConnection;
import org.example.model.UserGame;
import org.example.session.UserSession;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GamesController extends DashboardController {

    @FXML
    private Label availableGamesCountLabel;

    @FXML
    private TextField searchGameField;

    @FXML
    private Label featuredGameSmallTitleLabel;

    @FXML
    private Label featuredGameTitleLabel;

    @FXML
    private Label featuredGameDescriptionLabel;

    @FXML
    private Label featuredTeamsCountLabel;

    @FXML
    private Label featuredMatchesCountLabel;

    @FXML
    private Label featuredTournamentsCountLabel;

    @FXML
    private Label game1TitleLabel;

    @FXML
    private Label game1CategoryLabel;

    @FXML
    private Label game1PriceLabel;

    @FXML
    private Label game1DeveloperLabel;

    @FXML
    private Label game2TitleLabel;

    @FXML
    private Label game2CategoryLabel;

    @FXML
    private Label game2PriceLabel;

    @FXML
    private Label game2DeveloperLabel;

    @FXML
    private Label game3TitleLabel;

    @FXML
    private Label game3CategoryLabel;

    @FXML
    private Label game3PriceLabel;

    @FXML
    private Label game3DeveloperLabel;

    private final List<UserGame> allGames = new ArrayList<>();
    private final ObservableList<UserGame> shownGames = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadGamesFromDatabase();
        applyFilter("");
    }

    private void loadGamesFromDatabase() {
        allGames.clear();

        String sql = """
                SELECT g.game_id, g.title, g.price, g.release_date, g.developer_name, c.category_name
                FROM Game g
                LEFT JOIN GameCategory c ON g.category_id = c.category_id
                ORDER BY g.title
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                allGames.add(new UserGame(
                        resultSet.getInt("game_id"),
                        resultSet.getString("title"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getDate("release_date") != null
                                ? resultSet.getDate("release_date").toLocalDate()
                                : null,
                        resultSet.getString("developer_name"),
                        resultSet.getString("category_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (availableGamesCountLabel != null) {
            availableGamesCountLabel.setText(String.valueOf(allGames.size()));
        }
    }

    private void applyFilter(String raw) {
        String q = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);

        List<UserGame> filtered = allGames.stream()
                .filter(g -> q.isEmpty()
                        || (g.getTitle() != null && g.getTitle().toLowerCase(Locale.ROOT).contains(q))
                        || (g.getDeveloperName() != null && g.getDeveloperName().toLowerCase(Locale.ROOT).contains(q))
                        || (g.getCategoryName() != null && g.getCategoryName().toLowerCase(Locale.ROOT).contains(q)))
                .collect(Collectors.toList());

        shownGames.setAll(filtered);
        refreshCards();
    }

    private void refreshCards() {
        UserGame featured = shownGames.isEmpty() ? null : shownGames.get(0);
        if (featuredGameTitleLabel != null) {
            if (featured == null) {
                setLabel(featuredGameTitleLabel, "No games");
                setLabel(featuredGameDescriptionLabel, "No data loaded from the database.");
                setLabel(featuredTeamsCountLabel, "0");
                setLabel(featuredMatchesCountLabel, "0");
                setLabel(featuredTournamentsCountLabel, "0");
            } else {
                setLabel(featuredGameTitleLabel, featured.getTitle());
                setLabel(featuredGameDescriptionLabel, buildGameDescription(featured));
                setLabel(featuredTeamsCountLabel, String.valueOf(countTeamsForGame(featured.getGameId())));
                setLabel(featuredMatchesCountLabel, String.valueOf(countMatchesForGame(featured.getGameId())));
                setLabel(featuredTournamentsCountLabel, String.valueOf(countTournamentsForGame(featured.getGameId())));
            }
        }

        fillCard(0, game1TitleLabel, game1CategoryLabel, game1PriceLabel, game1DeveloperLabel);
        fillCard(1, game2TitleLabel, game2CategoryLabel, game2PriceLabel, game2DeveloperLabel);
        fillCard(2, game3TitleLabel, game3CategoryLabel, game3PriceLabel, game3DeveloperLabel);
    }

    private void fillCard(int index, Label title, Label category, Label price, Label developer) {
        if (title == null) {
            return;
        }
        if (index >= shownGames.size()) {
            setLabel(title, "—");
            setLabel(category, "Category: —");
            setLabel(price, "Price: —");
            setLabel(developer, "Developer: —");
            return;
        }
        UserGame g = shownGames.get(index);
        setLabel(title, g.getTitle());
        setLabel(category, "Category: " + nullToDash(g.getCategoryName()));
        setLabel(price, "Price: " + formatPrice(g.getPrice()));
        setLabel(developer, "Developer: " + nullToDash(g.getDeveloperName()));
    }

    private static String nullToDash(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }

    private static String formatPrice(BigDecimal price) {
        if (price == null) {
            return "—";
        }
        return price.compareTo(BigDecimal.ZERO) == 0 ? "Free" : price.toPlainString();
    }

    private static String buildGameDescription(UserGame g) {
        String dev = g.getDeveloperName() == null ? "" : g.getDeveloperName();
        String cat = g.getCategoryName() == null ? "" : g.getCategoryName();
        String rel = g.getReleaseDate() == null ? "—" : g.getReleaseDate().toString();
        return (dev.isEmpty() && cat.isEmpty())
                ? ("Release: " + rel)
                : (dev + (cat.isEmpty() ? "" : " · " + cat) + " · Release: " + rel);
    }

    private static void setLabel(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    private int countTournamentsForGame(int gameId) {
        String sql = "SELECT COUNT(*) AS total FROM Tournament WHERE game_id = ?";
        return countInt(sql, gameId);
    }

    private int countMatchesForGame(int gameId) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM MatchGame mg
                JOIN Tournament t ON mg.tournament_id = t.tournament_id
                WHERE t.game_id = ?
                """;
        return countInt(sql, gameId);
    }

    private int countTeamsForGame(int gameId) {
        String sql = """
                SELECT COUNT(DISTINCT tp.team_id) AS total
                FROM TournamentParticipation tp
                JOIN Tournament t ON tp.tournament_id = t.tournament_id
                WHERE t.game_id = ?
                """;
        return countInt(sql, gameId);
    }

    private int countInt(String sql, int param) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, param);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @FXML
    public void searchGame(ActionEvent event) {
        String keyword = searchGameField == null ? "" : searchGameField.getText();
        applyFilter(keyword);
    }

    @FXML
    public void viewGame1Details(ActionEvent event) {
        showGameDetails(0);
    }

    @FXML
    public void viewGame2Details(ActionEvent event) {
        showGameDetails(1);
    }

    @FXML
    public void viewGame3Details(ActionEvent event) {
        showGameDetails(2);
    }

    private void showGameDetails(int indexInShown) {
        if (indexInShown < 0 || indexInShown >= shownGames.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No game in this slot.").showAndWait();
            return;
        }
        UserGame g = shownGames.get(indexInShown);
        String body = "ID: " + g.getGameId()
                + "\nTitle: " + g.getTitle()
                + "\nCategory: " + nullToDash(g.getCategoryName())
                + "\nDeveloper: " + nullToDash(g.getDeveloperName())
                + "\nPrice: " + formatPrice(g.getPrice())
                + "\nRelease: " + (g.getReleaseDate() == null ? "—" : g.getReleaseDate().toString())
                + "\nTournaments: " + countTournamentsForGame(g.getGameId())
                + "\nMatches: " + countMatchesForGame(g.getGameId())
                + "\nTeams (via tournaments): " + countTeamsForGame(g.getGameId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game details");
        alert.setHeaderText(g.getTitle());
        alert.setContentText(body);
        alert.showAndWait();
    }

    @FXML
    public void joinFeaturedGame(ActionEvent event) {
        joinGameAtIndex(0);
    }

    @FXML
    public void joinGame1(ActionEvent event) {
        joinGameAtIndex(0);
    }

    @FXML
    public void joinGame2(ActionEvent event) {
        joinGameAtIndex(1);
    }

    @FXML
    public void joinGame3(ActionEvent event) {
        joinGameAtIndex(2);
    }

    private void joinGameAtIndex(int shownIndex) {
        int playerId = UserSession.getPlayerId();
        if (playerId <= 0) {
            new Alert(Alert.AlertType.WARNING, "Please sign in with a player account to join a game.").showAndWait();
            return;
        }
        if (shownIndex < 0 || shownIndex >= shownGames.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No game in this slot.").showAndWait();
            return;
        }
        UserGame game = shownGames.get(shownIndex);
        int gameId = game.getGameId();
        BigDecimal amount = game.getPrice() != null ? game.getPrice() : BigDecimal.ZERO;

        String existsSql = "SELECT 1 FROM Purchase WHERE player_id = ? AND game_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(existsSql)) {
            check.setInt(1, playerId);
            check.setInt(2, gameId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    new Alert(Alert.AlertType.INFORMATION, "You already joined/own this game.").showAndWait();
                    return;
                }
            }

            String insertSql = """
                    INSERT INTO Purchase (player_id, game_id, purchase_date, amount, payment_method, status)
                    VALUES (?, ?, CURDATE(), ?, 'In App', 'Completed')
                    """;
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setInt(1, playerId);
                insert.setInt(2, gameId);
                insert.setBigDecimal(3, amount);
                insert.executeUpdate();
            }
            new Alert(Alert.AlertType.INFORMATION, "You joined the game successfully.").showAndWait();
            loadGamesFromDatabase();
            applyFilter(searchGameField == null ? "" : searchGameField.getText());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not join game: " + e.getMessage()).showAndWait();
        }
    }
}
