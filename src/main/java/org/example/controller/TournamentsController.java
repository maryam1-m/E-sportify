package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TournamentsController extends DashboardController {

    @FXML
    private TextField searchTournamentField;

    public void searchTournament(ActionEvent event) {
        System.out.println("Searching tournament: " + searchTournamentField.getText());
    }

    public void addTournament(ActionEvent event) {
        System.out.println("Add tournament clicked");
    }

    public void viewTournamentDetails(ActionEvent event) {
        System.out.println("View tournament details clicked");
    }
}