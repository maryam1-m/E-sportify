package org.example.controller;

import javafx.event.ActionEvent;

public class AdminDashboardController {

    public void goToDashboard(ActionEvent event) throws Exception {
        Navigation.goTo(event, "DashboardAdmin.fxml");
    }

    public void goToPlayers(ActionEvent event) throws Exception {
        Navigation.goTo(event, "playersadmin.fxml");
    }

    public void goToTeams(ActionEvent event) throws Exception {
        Navigation.goTo(event, "Teamsadmin.fxml");
    }

    public void goToTournaments(ActionEvent event) throws Exception {
        Navigation.goTo(event, "TournmentAdmin.fxml");
    }

    public void goToMatches(ActionEvent event) throws Exception {
        Navigation.goTo(event, "MatchesAdmin.fxml");
    }

    public void goToGames(ActionEvent event) throws Exception {
        Navigation.goTo(event, "GamesAdmin.fxml");
    }

    public void goToSeasons(ActionEvent event) throws Exception {
        Navigation.goTo(event, "SeasonsAdmin.fxml");
    }

    public void logout(ActionEvent event) throws Exception {
        Navigation.goTo(event, "signinFrame.fxml");
    }

    public void refreshDashboard(ActionEvent event) {
        System.out.println("Dashboard refreshed");
    }
}