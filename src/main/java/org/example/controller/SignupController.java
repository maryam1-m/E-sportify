package org.example.controller;

import javafx.event.ActionEvent;

public class SignupController {

    public void handleSignup(ActionEvent event) throws Exception {
        Navigation.goTo(event, "dashboardInterface.fxml");
    }

    public void goBack(ActionEvent event) throws Exception {
        Navigation.goTo(event, "e-sportify.fxml");
    }
}