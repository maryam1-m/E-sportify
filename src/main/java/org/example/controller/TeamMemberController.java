package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TeamMemberController extends DashboardController {

    @FXML
    private TextField searchMemberField;

    public void searchMember(ActionEvent event) {
        System.out.println("Searching member: " + searchMemberField.getText());
    }

    public void addTeamMember(ActionEvent event) {
        System.out.println("Add team member clicked");
    }

    public void viewMemberDetails(ActionEvent event) {
        System.out.println("View member details clicked");
    }
}