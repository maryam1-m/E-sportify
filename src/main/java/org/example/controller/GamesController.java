package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class GamesController extends DashboardController {

    @FXML
    private TextField searchGameField;

    public void searchGame(ActionEvent event) {
        System.out.println("Searching: " + searchGameField.getText());
    }

    public void viewGameDetails(ActionEvent event) {
        System.out.println("View game details clicked");
    }
}