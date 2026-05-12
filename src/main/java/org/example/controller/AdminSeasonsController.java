package org.example.controller;

import javafx.event.ActionEvent;

public class AdminSeasonsController extends AdminDashboardController {

    public void addSeason(ActionEvent event) {
        System.out.println("Add season clicked");
    }

    public void updateSeason(ActionEvent event) {
        System.out.println("Update season clicked");
    }

    public void deleteSeason(ActionEvent event) {
        System.out.println("Delete season clicked");
    }

    public void clearFields(ActionEvent event) {
        System.out.println("Clear season fields clicked");
    }

    public void searchSeasons(ActionEvent event) {
        System.out.println("Search seasons clicked");
    }

    public void loadSeasons(ActionEvent event) {
        System.out.println("Load seasons clicked");
    }
}