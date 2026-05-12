package org.example.model;

import java.time.LocalDate;

public class UserMatch {

    private int matchId;
    private String tournamentName;
    private LocalDate matchDate;
    private String teams;
    private String result;

    public UserMatch(int matchId, String tournamentName, LocalDate matchDate, String teams, String result) {
        this.matchId = matchId;
        this.tournamentName = tournamentName;
        this.matchDate = matchDate;
        this.teams = teams;
        this.result = result;
    }

    public int getMatchId() {
        return matchId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public String getTeams() {
        return teams;
    }

    public String getResult() {
        return result;
    }
}