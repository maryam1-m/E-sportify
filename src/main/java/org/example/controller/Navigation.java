package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class Navigation {

    private Navigation() {
    }

    public static void goTo(ActionEvent event, String fxmlFile) throws Exception {
        URL url = Navigation.class.getResource("/" + fxmlFile);
        if (url == null) {
            throw new IllegalStateException("FXML not found on classpath: /" + fxmlFile);
        }
        FXMLLoader loader = new FXMLLoader(url);
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
