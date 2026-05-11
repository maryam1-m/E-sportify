package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {

    public static void goTo(ActionEvent event, String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Navigation.class.getResource("/" + fxmlFile)
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}