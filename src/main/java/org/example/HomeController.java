package org.example.controller;

import javafx.event.ActionEvent;

public class HomeController {

    public void goToLogin(ActionEvent event) throws Exception {
        Navigation.goTo(event, "signInFrame.fxml");
    }

    public void goToSignup(ActionEvent event) throws Exception {
        Navigation.goTo(event, "signuoFrame.fxml");
    }
}