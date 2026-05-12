package org.example.controller;

import javafx.event.ActionEvent;

public class AdminPlayersController extends AdminDashboardController {

    public void addPlayer(ActionEvent event) {
        System.out.println("Add player clicked");
    }

    public void updatePlayer(ActionEvent event) {
        System.out.println("Update player clicked");
    }

    public void deletePlayer(ActionEvent event) {
        System.out.println("Delete player clicked");
    }

    public void clearFields(ActionEvent event) {
        System.out.println("Clear player fields clicked");
    }
}