package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeasonsController extends DashboardController {

    @FXML private Label currentSeasonLabel;
    @FXML private Label currentSeasonStatusSmallLabel;
    @FXML private Label seasonTournamentsCountLabel;
    @FXML private Label registeredTeamsCountLabel;

    @FXML private Label featuredSeasonNameLabel;
    @FXML private Label featuredStartDateLabel;
    @FXML private Label featuredEndDateLabel;
    @FXML private Label featuredStatusLabel;
    @FXML private Label featuredGamesLabel;

    @FXML private Label season1NameLabel;
    @FXML private Label season1DatesLabel;
    @FXML private Label season1StatusLabel;

    @FXML private Label season2NameLabel;
    @FXML private Label season2DatesLabel;
    @FXML private Label season2StatusLabel;

    @FXML private Label season3NameLabel;
    @FXML private Label season3DatesLabel;
    @FXML private Label season3StatusLabel;

    @FXML private Label season4NameLabel;
    @FXML private Label season4DatesLabel;
    @FXML private Label season4StatusLabel;

    @FXML
    public void initialize() {
        loadSeasons();
    }

    private void loadSeasons() {
        List<SeasonRow> seasons = new ArrayList<>();

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
                LIMIT 4
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();

                seasons.add(new SeasonRow(
                        resultSet.getString("season_name"),
                        startDate,
                        endDate,
                        getStatus(startDate, endDate),
                        resultSet.getInt("tournaments_count"),
                        resultSet.getString("games") == null ? "No Games" : resultSet.getString("games")
                ));
            }

            setText(registeredTeamsCountLabel, String.valueOf(count("SELECT COUNT(*) AS total FROM Team")));

            SeasonRow featured = chooseFeaturedSeason(seasons);

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

            fillSeason(0, seasons, season1NameLabel, season1DatesLabel, season1StatusLabel);
            fillSeason(1, seasons, season2NameLabel, season2DatesLabel, season2StatusLabel);
            fillSeason(2, seasons, season3NameLabel, season3DatesLabel, season3StatusLabel);
            fillSeason(3, seasons, season4NameLabel, season4DatesLabel, season4StatusLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String name;
        LocalDate startDate;
        LocalDate endDate;
        String status;
        int tournamentsCount;
        String games;

        SeasonRow(String name, LocalDate startDate, LocalDate endDate, String status, int tournamentsCount, String games) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.tournamentsCount = tournamentsCount;
            this.games = games;
        }
    }
}