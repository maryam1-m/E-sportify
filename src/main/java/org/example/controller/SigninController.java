package org.example.controller;

import javafx.event.ActionEvent;

public class SigninController {

    public void handleLogin(ActionEvent event) throws Exception {
        Navigation.goTo(event, "dashboardInterface.fxml");
    }

    public void goBack(ActionEvent event) throws Exception {
        Navigation.goTo(event, "e-sportify.fxml");
    }
}