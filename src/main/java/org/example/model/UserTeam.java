package org.example.model;

import java.time.LocalDate;

public class UserTeam {

    private int teamId;
    private String teamName;
    private LocalDate creationDate;
    private int membersCount;

    public UserTeam(int teamId, String teamName, LocalDate creationDate, int membersCount) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.creationDate = creationDate;
        this.membersCount = membersCount;
    }

    public int getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public int getMembersCount() {
        return membersCount;
    }
}