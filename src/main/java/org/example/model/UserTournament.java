package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserTournament {

    private int tournamentId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal prizePool;
    private String gameTitle;
    private String seasonName;

    public UserTournament(int tournamentId, String name, LocalDate startDate, LocalDate endDate,
                          BigDecimal prizePool, String gameTitle, String seasonName) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizePool = prizePool;
        this.gameTitle = gameTitle;
        this.seasonName = seasonName;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPrizePool() {
        return prizePool;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getSeasonName() {
        return seasonName;
    }
}