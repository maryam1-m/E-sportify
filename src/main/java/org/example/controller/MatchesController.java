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

public class MatchesController extends DashboardController {

    @FXML
    private Label totalMatchesCountLabel;
    @FXML
    private Label upcomingMatchesCountLabel;
    @FXML
    private Label finishedMatchesCountLabel;

    @FXML
    private Label match1DateLabel;
    @FXML
    private Label match1TeamsLabel;
    @FXML
    private Label match1StatusLabel;
    @FXML
    private Label match1ScoreLabel;

    @FXML
    private Label match2DateLabel;
    @FXML
    private Label match2TeamsLabel;
    @FXML
    private Label match2StatusLabel;
    @FXML
    private Label match2ScoreLabel;

    @FXML
    private Label match3DateLabel;
    @FXML
    private Label match3TeamsLabel;
    @FXML
    private Label match3StatusLabel;
    @FXML
    private Label match3ScoreLabel;

    @FXML
    private Label match4DateLabel;
    @FXML
    private Label match4TeamsLabel;
    @FXML
    private Label match4StatusLabel;
    @FXML
    private Label match4ScoreLabel;

    @FXML
    private Label match5DateLabel;
    @FXML
    private Label match5TeamsLabel;
    @FXML
    private Label match5StatusLabel;
    @FXML
    private Label match5ScoreLabel;

    @FXML
    private Label featuredMatchStatusLabel;
    @FXML
    private Label featuredMatchTeam1Label;
    @FXML
    private Label featuredMatchTeam2Label;
    @FXML
    private Label featuredMatchScore1Label;
    @FXML
    private Label featuredMatchScore2Label;
    @FXML
    private Label featuredMatchShortTeam1Label;
    @FXML
    private Label featuredMatchShortTeam2Label;

    private final List<MatchRow> matchRows = new ArrayList<>();
    private int filterMode;

    @FXML
    public void initialize() {
        loadMatches();
    }

    private void loadMatches() {
        matchRows.clear();

        String outerFilter = switch (filterMode % 4) {
            case 1 -> " WHERE sub.match_date > CURDATE() ";
            case 2 -> " WHERE sub.match_date < CURDATE() ";
            case 3 -> " WHERE sub.match_date = CURDATE() ";
            default -> "";
        };

        String sql = """
                SELECT sub.match_id, sub.match_date, sub.teams, sub.team_names, sub.score_text
                FROM (
                    SELECT
                        mg.match_id,
                        mg.match_date,
                        GROUP_CONCAT(t.team_name ORDER BY mp.final_rank SEPARATOR ' vs ') AS teams,
                        GROUP_CONCAT(t.team_name ORDER BY mp.final_rank SEPARATOR ',') AS team_names,
                        GROUP_CONCAT(mp.final_rank ORDER BY mp.final_rank SEPARATOR ' - ') AS score_text
                    FROM MatchGame mg
                    LEFT JOIN MatchParticipant mp ON mg.match_id = mp.match_id
                    LEFT JOIN Team t ON mp.team_id = t.team_id
                    GROUP BY mg.match_id, mg.match_date
                ) sub
                """ + outerFilter + """
                ORDER BY sub.match_date
                LIMIT 5
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                LocalDate matchDate = resultSet.getDate("match_date").toLocalDate();
                String teams = resultSet.getString("teams") == null ? "No Teams" : resultSet.getString("teams");
                String score = resultSet.getString("score_text") == null ? "-" : resultSet.getString("score_text");
                String teamNames = resultSet.getString("team_names") == null ? "" : resultSet.getString("team_names");

                matchRows.add(new MatchRow(
                        resultSet.getInt("match_id"),
                        matchDate.toString(),
                        teams,
                        getStatus(matchDate),
                        score,
                        teamNames
                ));
            }

            setText(totalMatchesCountLabel, String.valueOf(count("SELECT COUNT(*) AS total FROM MatchGame")));
            setText(upcomingMatchesCountLabel, String.valueOf(count("SELECT COUNT(*) AS total FROM MatchGame WHERE match_date > CURDATE()")));
            setText(finishedMatchesCountLabel, String.valueOf(count("SELECT COUNT(*) AS total FROM MatchGame WHERE match_date < CURDATE()")));

            fillMatch(0, matchRows, match1DateLabel, match1TeamsLabel, match1StatusLabel, match1ScoreLabel);
            fillMatch(1, matchRows, match2DateLabel, match2TeamsLabel, match2StatusLabel, match2ScoreLabel);
            fillMatch(2, matchRows, match3DateLabel, match3TeamsLabel, match3StatusLabel, match3ScoreLabel);
            fillMatch(3, matchRows, match4DateLabel, match4TeamsLabel, match4StatusLabel, match4ScoreLabel);
            fillMatch(4, matchRows, match5DateLabel, match5TeamsLabel, match5StatusLabel, match5ScoreLabel);

            fillFeaturedMatch(matchRows);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void filterMatches(ActionEvent event) {
        filterMode++;
        String label = switch (filterMode % 4) {
            case 1 -> "Upcoming";
            case 2 -> "Finished";
            case 3 -> "Today";
            default -> "All";
        };
        loadMatches();
        new Alert(Alert.AlertType.INFORMATION, "Filter: " + label).showAndWait();
    }

    @FXML
    public void viewFeaturedMatchDetails(ActionEvent event) {
        if (matchRows.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No match loaded.").showAndWait();
            return;
        }
        MatchRow m = matchRows.get(0);
        String body = "Match ID: " + m.matchId
                + "\nDate: " + m.date
                + "\nTeams: " + m.teams
                + "\nStatus: " + m.status
                + "\nRanks: " + m.score;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Match details");
        alert.setHeaderText(m.teams);
        alert.setContentText(body);
        alert.showAndWait();
    }

    private void fillFeaturedMatch(List<MatchRow> matches) {
        if (matches.isEmpty()) {
            setText(featuredMatchStatusLabel, "NO MATCH");
            setText(featuredMatchTeam1Label, "Team 1");
            setText(featuredMatchTeam2Label, "Team 2");
            setText(featuredMatchScore1Label, "-");
            setText(featuredMatchScore2Label, "-");
            setText(featuredMatchShortTeam1Label, "Team 1");
            setText(featuredMatchShortTeam2Label, "Team 2");
            return;
        }

        MatchRow featured = matches.get(0);
        String[] teams = featured.teamNames.split(",");
        String[] scores = featured.score.split(" - ");

        String team1 = teams.length > 0 && !teams[0].isBlank() ? teams[0] : "Team 1";
        String team2 = teams.length > 1 && !teams[1].isBlank() ? teams[1] : "Team 2";

        setText(featuredMatchStatusLabel, featured.status.toUpperCase() + " MATCH");
        setText(featuredMatchTeam1Label, team1);
        setText(featuredMatchTeam2Label, team2);
        setText(featuredMatchShortTeam1Label, team1);
        setText(featuredMatchShortTeam2Label, team2);

        setText(featuredMatchScore1Label, scores.length > 0 ? scores[0] : "-");
        setText(featuredMatchScore2Label, scores.length > 1 ? scores[1] : "-");
    }

    private void fillMatch(int index, List<MatchRow> matches, Label date, Label teams, Label status, Label score) {
        if (index < matches.size()) {
            MatchRow match = matches.get(index);
            setText(date, match.date);
            setText(teams, match.teams);
            setText(status, match.status);
            setText(score, match.score);
        } else {
            setText(date, "-");
            setText(teams, "-");
            setText(status, "-");
            setText(score, "-");
        }
    }

    private String getStatus(LocalDate date) {
        LocalDate today = LocalDate.now();

        if (date.isAfter(today)) {
            return "Upcoming";
        }

        if (date.isBefore(today)) {
            return "Finished";
        }

        return "Today";
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

    private static class MatchRow {
        int matchId;
        String date;
        String teams;
        String status;
        String score;
        String teamNames;

        MatchRow(int matchId, String date, String teams, String status, String score, String teamNames) {
            this.matchId = matchId;
            this.date = date;
            this.teams = teams;
            this.status = status;
            this.score = score;
            this.teamNames = teamNames;
        }
    }
}
