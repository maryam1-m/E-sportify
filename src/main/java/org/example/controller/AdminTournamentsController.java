package org.example.controller;

import javafx.event.ActionEvent;

public class AdminTournamentsController extends AdminDashboardController {

    public void addTournament(ActionEvent event) {
        System.out.println("Add tournament clicked");
    }

    public void updateTournament(ActionEvent event) {
        System.out.println("Update tournament clicked");
    }

    public void deleteTournament(ActionEvent event) {
        System.out.println("Delete tournament clicked");
    }

    public void clearFields(ActionEvent event) {
        System.out.println("Clear tournament fields clicked");
    }

    public void searchTournament(ActionEvent event) {
        System.out.println("Search tournament clicked");
    }

    public void refreshTable(ActionEvent event) {
        System.out.println("Refresh tournaments table clicked");
    }
}