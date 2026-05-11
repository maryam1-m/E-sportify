package org.example.controller;

import javafx.event.ActionEvent;

public class DashboardController {

    public void goToPlayers(ActionEvent event) throws Exception {
        System.out.println("Players clicked");
        // Navigation.goTo(event, "players.fxml");
    }

    public void goToTeams(ActionEvent event) throws Exception {
        System.out.println("Teams clicked");
        // Navigation.goTo(event, "teams.fxml");
    }

    public void goToTournaments(ActionEvent event) throws Exception {
        System.out.println("Tournaments clicked");
        // Navigation.goTo(event, "tournaments.fxml");
    }

    public void goToMatches(ActionEvent event) throws Exception {
        System.out.println("Matches clicked");
        // Navigation.goTo(event, "matches.fxml");
    }

    public void goToGames(ActionEvent event) throws Exception {
        System.out.println("Games clicked");
        // Navigation.goTo(event, "games.fxml");
    }

    public void goToSeasons(ActionEvent event) throws Exception {
        System.out.println("Seasons clicked");
        // Navigation.goTo(event, "seasons.fxml");
    }

    public void logout(ActionEvent event) throws Exception {
        Navigation.goTo(event, "signinFrame.fxml");
    }
}