package com.ft.app;

import com.ft.app.data.Db;
import com.ft.app.data.Seed;
import com.ft.app.ui.manage.ManageView;
import com.ft.app.ui.transactions.TransactionsView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    private BorderPane root;

    // Keep refs so we can toggle styles
    private Button btnTx;
    private Button btnManage;

    @Override
    public void start(Stage stage) {
        // DB init + seed
        Db.get();
        Seed.run();

        root = new BorderPane();
        root.setPadding(new Insets(10, 12, 10, 12));

        // Top navigation bar
        btnTx = new Button("Transactions");
        btnManage = new Button("Manage");

        btnTx.setOnAction(e -> {
            setActive(btnTx, btnManage);
            showTransactions();
        });

        btnManage.setOnAction(e -> {
            setActive(btnManage, btnTx);
            showManage();
        });

        var nav = new ToolBar(btnTx, new Separator(), btnManage);
        root.setTop(nav);

        // Default view + active state
        setActive(btnTx, btnManage);
        showTransactions();

        var scene = new Scene(root, 1000, 700);
        var cssUrl = getClass().getResource("/com/ft/app/app.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        stage.setTitle("Ledger — Personal Finance (CAD)");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> Db.closeQuietly());
        stage.show();
    }

    private void setActive(Button active, Button inactive) {
        if (!active.getStyleClass().contains("btn-primary")) {
            active.getStyleClass().add("btn-primary");
        }
        inactive.getStyleClass().remove("btn-primary");
    }

    private void showTransactions() {
        root.setCenter(new TransactionsView());
    }

    private void showManage() {
        root.setCenter(new ManageView());
    }

    public static void main(String[] args) { launch(); }
}
