package org.example.controller;

import javafx.event.ActionEvent;

public class AdminGamesController extends AdminDashboardController {

    public void addGame(ActionEvent event) {
        System.out.println("Add game clicked");
    }

    public void updateGame(ActionEvent event) {
        System.out.println("Update game clicked");
    }

    public void deleteGame(ActionEvent event) {
        System.out.println("Delete game clicked");
    }

    public void clearFields(ActionEvent event) {
        System.out.println("Clear game fields clicked");
    }

    public void searchGames(ActionEvent event) {
        System.out.println("Search games clicked");
    }

    public void loadGames(ActionEvent event) {
        System.out.println("Load games clicked");
    }
}