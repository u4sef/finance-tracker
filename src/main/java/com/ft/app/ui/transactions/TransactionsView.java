package com.ft.app.ui.transactions;

import com.ft.app.data.dao.TxDao;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.stream.Collectors;

public class TransactionsView extends BorderPane {
    private final TableView<TransactionVm> table = new TableView<>();
    private final Label status = new Label();

    public TransactionsView() {
        setTop(buildHeader());
        setCenter(buildTable());
        setBottom(buildStatus());

        loadData();
    }

    private Node buildHeader() {
        var title = new Label("Transactions");
        title.getStyleClass().add("h2");
        var box = new ToolBar(title);
        return box;
    }

    private Node buildTable() {
        var colDate = new TableColumn<TransactionVm, Object>("Date");
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));
        colDate.setPrefWidth(120);

        var colPayee = new TableColumn<TransactionVm, String>("Payee");
        colPayee.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("payee"));
        colPayee.setPrefWidth(220);

        var colCategory = new TableColumn<TransactionVm, String>("Category");
        colCategory.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));
        colCategory.setPrefWidth(160);

        var colAccount = new TableColumn<TransactionVm, String>("Account");
        colAccount.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("account"));
        colAccount.setPrefWidth(140);

        var colAmount = new TableColumn<TransactionVm, String>("Amount");
        colAmount.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(120);
        colAmount.setStyle("-fx-alignment: CENTER-RIGHT;");

        var colNote = new TableColumn<TransactionVm, String>("Note");
        colNote.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("note"));
        colNote.setPrefWidth(240);

        table.getColumns().addAll(colDate, colPayee, colCategory, colAccount, colAmount, colNote);
        table.setPlaceholder(new Label("No transactions yet"));

        var stack = new StackPane(table);
        return stack;
    }

    private Node buildStatus() {
        var bar = new ToolBar(status);
        status.setText("0 items");
        return bar;
    }

    private void loadData() {
        try {
            var rows = new TxDao().listAll();
            var vms = rows.stream().map(r ->
                            new TransactionVm(r.id(), r.dt(), r.payee(), r.categoryName(),
                                    r.accountName(), r.amountCents(), r.note()))
                    .collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(vms));
            status.setText(vms.size() + (vms.size() == 1 ? " item" : " items"));
        } catch (Exception e) {
            table.setPlaceholder(new Label("Failed to load transactions"));
            e.printStackTrace();
        }
    }
}
