package com.ft.app.ui.manage;

import com.ft.app.data.dao.AccountDao;
import com.ft.app.data.dao.CategoryDao;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.stream.Collectors;

public class ManageView extends BorderPane {
    private final TableView<AccountVm> accountsTable = new TableView<>();
    private final TableView<CategoryVm> categoriesTable = new TableView<>();

    public ManageView() {
        setTop(buildHeader());
        var tabs = new TabPane(
                new Tab("Accounts", wrap(accountsTable)),
                new Tab("Categories", wrap(categoriesTable))
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        setCenter(tabs);
        setPadding(new Insets(10,12,10,12));

        setupAccountsTable();
        setupCategoriesTable();
        reload();
    }

    private Node buildHeader() {
        var title = new Label("Accounts & Categories");
        title.getStyleClass().add("h2");
        return new ToolBar(title);
    }

    private void setupAccountsTable() {
        var name = new TableColumn<AccountVm, String>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        var type = new TableColumn<AccountVm, String>("Type");
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        var currency = new TableColumn<AccountVm, String>("Currency");
        currency.setCellValueFactory(new PropertyValueFactory<>("currency"));

        accountsTable.getColumns().setAll(name, type, currency);
        accountsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        accountsTable.setPlaceholder(new Label("No accounts"));
    }

    private void setupCategoriesTable() {
        var name = new TableColumn<CategoryVm, String>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        var kind = new TableColumn<CategoryVm, String>("Kind");
        kind.setCellValueFactory(new PropertyValueFactory<>("kind"));

        categoriesTable.getColumns().setAll(name, kind);
        categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        categoriesTable.setPlaceholder(new Label("No categories"));
    }

    private Node wrap(TableView<?> tv) { return new StackPane(tv); }

    private void reload() {
        try {
            var accs = new AccountDao().findAll().stream()
                    .map(a -> new AccountVm(a.id(), a.name(), a.type(), a.currency()))
                    .collect(Collectors.toList());
            accountsTable.setItems(FXCollections.observableArrayList(accs));

            var cats = new CategoryDao().findAll().stream()
                    .map(c -> new CategoryVm(c.id(), c.name(), c.kind()))
                    .collect(Collectors.toList());
            categoriesTable.setItems(FXCollections.observableArrayList(cats));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
