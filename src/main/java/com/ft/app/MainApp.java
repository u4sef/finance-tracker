package com.ft.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        var root = new BorderPane();
        var scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/com/ft/app/app.css").toExternalForm());
        stage.setTitle("Ledger — Personal Finance (CAD)");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}
