package org.example.controller;

import javafx.event.ActionEvent;

public class DashboardController {

    public void goToDashboard(ActionEvent event) throws Exception {
        Navigation.goTo(event, "dashboardInterface.fxml");
    }

    public void goToPlayers(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Players.fxml");
    }

    public void goToTeams(ActionEvent event) throws Exception {
        System.out.println("Teams clicked");
        // Navigation.goTo(event, "Teams.fxml");
    }

    public void goToTournaments(ActionEvent event) throws Exception {
        System.out.println("Tournaments clicked");
        // Navigation.goTo(event, "Tournaments.fxml");
    }

    public void goToMatches(ActionEvent event) throws Exception {
        System.out.println("Matches clicked");
        // Navigation.goTo(event, "Matches.fxml");
    }

    public void goToGames(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Games.fxml");
    }

    public void goToSeasons(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Seasons.fxml");
    }

    public void logout(ActionEvent event) throws Exception {
        Navigation.goTo(event, "signinFrame.fxml");
    }
}