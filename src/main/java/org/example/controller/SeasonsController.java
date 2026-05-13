package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeasonsController extends DashboardController {

    @FXML
    private Label currentSeasonLabel;
    @FXML
    private Label currentSeasonStatusSmallLabel;
    @FXML
    private Label seasonTournamentsCountLabel;
    @FXML
    private Label registeredTeamsCountLabel;

    @FXML
    private Label featuredSeasonNameLabel;
    @FXML
    private Label featuredStartDateLabel;
    @FXML
    private Label featuredEndDateLabel;
    @FXML
    private Label featuredStatusLabel;
    @FXML
    private Label featuredGamesLabel;

    @FXML
    private Label season1NameLabel;
    @FXML
    private Label season1DatesLabel;
    @FXML
    private Label season1StatusLabel;

    @FXML
    private Label season2NameLabel;
    @FXML
    private Label season2DatesLabel;
    @FXML
    private Label season2StatusLabel;

    @FXML
    private Label season3NameLabel;
    @FXML
    private Label season3DatesLabel;
    @FXML
    private Label season3StatusLabel;

    @FXML
    private Label season4NameLabel;
    @FXML
    private Label season4DatesLabel;
    @FXML
    private Label season4StatusLabel;

    private final List<SeasonRow> allSeasonRows = new ArrayList<>();
    private final List<SeasonRow> displaySeasonRows = new ArrayList<>();
    private int filterMode;

    @FXML
    public void initialize() {
        loadSeasons();
    }

    private void loadSeasons() {
        allSeasonRows.clear();

        String sql = """
                SELECT
                    s.season_id,
                    s.season_name,
                    s.start_date,
                    s.end_date,
                    COUNT(DISTINCT t.tournament_id) AS tournaments_count,
                    GROUP_CONCAT(DISTINCT g.title SEPARATOR ', ') AS games
                FROM Season s
                LEFT JOIN Tournament t ON s.season_id = t.season_id
                LEFT JOIN Game g ON t.game_id = g.game_id
                GROUP BY s.season_id, s.season_name, s.start_date, s.end_date
                ORDER BY s.start_date
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();

                allSeasonRows.add(new SeasonRow(
                        resultSet.getInt("season_id"),
                        resultSet.getString("season_name"),
                        startDate,
                        endDate,
                        getStatus(startDate, endDate),
                        resultSet.getInt("tournaments_count"),
                        resultSet.getString("games") == null ? "No Games" : resultSet.getString("games")
                ));
            }

            setText(registeredTeamsCountLabel, String.valueOf(count("SELECT COUNT(*) AS total FROM Team")));

            applyFilter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilter() {
        String target = switch (filterMode % 4) {
            case 1 -> "Active";
            case 2 -> "Upcoming";
            case 3 -> "Finished";
            default -> null;
        };

        List<SeasonRow> source = target == null
                ? allSeasonRows
                : allSeasonRows.stream().filter(s -> target.equalsIgnoreCase(s.status)).collect(Collectors.toList());

        displaySeasonRows.clear();
        for (int i = 0; i < Math.min(4, source.size()); i++) {
            displaySeasonRows.add(source.get(i));
        }

        SeasonRow featured = chooseFeaturedSeason(displaySeasonRows.isEmpty() ? allSeasonRows : displaySeasonRows);

        if (featured != null) {
            setText(currentSeasonLabel, featured.name);
            setText(currentSeasonStatusSmallLabel, featured.status.equals("Active") ? "Active now" : featured.status);
            setText(seasonTournamentsCountLabel, String.valueOf(featured.tournamentsCount));

            setText(featuredSeasonNameLabel, featured.name);
            setText(featuredStartDateLabel, "Start Date: " + featured.startDate);
            setText(featuredEndDateLabel, "End Date: " + featured.endDate);
            setText(featuredStatusLabel, "Status: " + featured.status);
            setText(featuredGamesLabel, "Games: " + featured.games);
        } else {
            setText(currentSeasonLabel, "-");
            setText(currentSeasonStatusSmallLabel, "-");
            setText(seasonTournamentsCountLabel, "0");

            setText(featuredSeasonNameLabel, "-");
            setText(featuredStartDateLabel, "Start Date: -");
            setText(featuredEndDateLabel, "End Date: -");
            setText(featuredStatusLabel, "Status: -");
            setText(featuredGamesLabel, "Games: -");
        }

        fillSeason(0, displaySeasonRows, season1NameLabel, season1DatesLabel, season1StatusLabel);
        fillSeason(1, displaySeasonRows, season2NameLabel, season2DatesLabel, season2StatusLabel);
        fillSeason(2, displaySeasonRows, season3NameLabel, season3DatesLabel, season3StatusLabel);
        fillSeason(3, displaySeasonRows, season4NameLabel, season4DatesLabel, season4StatusLabel);
    }

    @FXML
    public void filterSeasons(ActionEvent event) {
        filterMode++;
        applyFilter();
        String label = switch (filterMode % 4) {
            case 1 -> "Active";
            case 2 -> "Upcoming";
            case 3 -> "Finished";
            default -> "All";
        };
        new Alert(Alert.AlertType.INFORMATION, "Filter: " + label).showAndWait();
    }

    @FXML
    public void viewFeaturedSeasonDetails(ActionEvent event) {
        SeasonRow featured = chooseFeaturedSeason(displaySeasonRows.isEmpty() ? allSeasonRows : displaySeasonRows);
        if (featured == null) {
            new Alert(Alert.AlertType.INFORMATION, "No season data.").showAndWait();
            return;
        }
        showSeasonAlert(featured);
    }

    @FXML
    public void openSeason1(ActionEvent event) {
        showSeasonAt(0);
    }

    @FXML
    public void openSeason2(ActionEvent event) {
        showSeasonAt(1);
    }

    @FXML
    public void openSeason3(ActionEvent event) {
        showSeasonAt(2);
    }

    @FXML
    public void openSeason4(ActionEvent event) {
        showSeasonAt(3);
    }

    private void showSeasonAt(int index) {
        if (index < 0 || index >= displaySeasonRows.size()) {
            new Alert(Alert.AlertType.INFORMATION, "No season in this slot.").showAndWait();
            return;
        }
        showSeasonAlert(displaySeasonRows.get(index));
    }

    private void showSeasonAlert(SeasonRow s) {
        String body = "ID: " + s.seasonId
                + "\nName: " + s.name
                + "\nStart: " + s.startDate
                + "\nEnd: " + s.endDate
                + "\nStatus: " + s.status
                + "\nTournaments: " + s.tournamentsCount
                + "\nGames: " + s.games;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Season details");
        alert.setHeaderText(s.name);
        alert.setContentText(body);
        alert.showAndWait();
    }

    private SeasonRow chooseFeaturedSeason(List<SeasonRow> seasons) {
        if (seasons.isEmpty()) {
            return null;
        }

        for (SeasonRow season : seasons) {
            if ("Active".equalsIgnoreCase(season.status)) {
                return season;
            }
        }

        return seasons.get(0);
    }

    private void fillSeason(int index, List<SeasonRow> seasons, Label nameLabel, Label datesLabel, Label statusLabel) {
        if (index < seasons.size()) {
            SeasonRow season = seasons.get(index);

            setText(nameLabel, season.name);
            setText(datesLabel, season.startDate + " - " + season.endDate);
            setText(statusLabel, "Status: " + season.status + " | " + season.tournamentsCount + " tournaments");
        } else {
            setText(nameLabel, "-");
            setText(datesLabel, "-");
            setText(statusLabel, "-");
        }
    }

    private String getStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(startDate)) {
            return "Upcoming";
        }

        if (today.isAfter(endDate)) {
            return "Finished";
        }

        return "Active";
    }

    private int count(String sql) {
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

    private void setText(Label label, String value) {
        if (label != null) {
            label.setText(value);
        }
    }

    private static class SeasonRow {
        int seasonId;
        String name;
        LocalDate startDate;
        LocalDate endDate;
        String status;
        int tournamentsCount;
        String games;

        SeasonRow(int seasonId, String name, LocalDate startDate, LocalDate endDate, String status, int tournamentsCount, String games) {
            this.seasonId = seasonId;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.tournamentsCount = tournamentsCount;
            this.games = games;
        }
    }
}
