package com.ft.app;

import com.ft.app.data.Db;
import com.ft.app.data.Seed;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        // Open DB and seed defaults
        try {

            Db.get();
            Seed.run();

            var root = new BorderPane();
            root.setPadding(new Insets(10, 12, 10, 12));
            root.setCenter(new com.ft.app.ui.transactions.TransactionsView());
            var scene = new Scene(root, 1000, 700);

            var cssUrl = getClass().getResource("/com/ft/app/app.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());


            stage.setTitle("Ledger — Personal Finance (CAD)");
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> Db.closeQuietly());
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static void main(String[] args) { launch(); }
}
