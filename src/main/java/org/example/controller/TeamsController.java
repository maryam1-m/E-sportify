package org.example.controller;

import javafx.event.ActionEvent;

public class TeamsController extends AdminDashboardController {

    public void addTeam(ActionEvent event) {
        System.out.println("Add team clicked");
    }

    public void updateTeam(ActionEvent event) {
        System.out.println("Update team clicked");
    }

    public void deleteTeam(ActionEvent event) {
        System.out.println("Delete team clicked");
    }

    public void clearFields(ActionEvent event) {
        System.out.println("Clear team fields clicked");
    }
}