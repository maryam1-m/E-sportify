package org.example.model;

import java.time.LocalDate;

public class UserSeason {

    private int seasonId;
    private String seasonName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int tournamentCount;

    public UserSeason(int seasonId, String seasonName, LocalDate startDate, LocalDate endDate, int tournamentCount) {
        this.seasonId = seasonId;
        this.seasonName = seasonName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tournamentCount = tournamentCount;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTournamentCount() {
        return tournamentCount;
    }
}