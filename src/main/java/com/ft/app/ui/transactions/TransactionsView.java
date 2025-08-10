package com.ft.app.ui.transactions;

import com.ft.app.data.dao.TxDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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

        var addBtn = new Button("Add");
        addBtn.setOnAction(e -> openAddDialog()); // we'll create this method next

        return new ToolBar(title, new Separator(), addBtn);
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

    private void openAddDialog() {
        var dlg = new Dialog<Boolean>();
        dlg.setTitle("Add Transaction");

        var saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        // Fields
        var datePicker = new DatePicker(java.time.LocalDate.now());
        var payeeField = new TextField();
        var amountField = new TextField(); // we'll parse to cents
        var noteField = new TextField();

        var accountCombo = new ComboBox<String>();
        var categoryCombo = new ComboBox<String>();

        // Load account/category names (simple for now)
        try {
            var accounts = new com.ft.app.data.dao.AccountDao().findAll();
            accountCombo.getItems().addAll(accounts.stream().map(a -> a.name()).toList());
            if (!accountCombo.getItems().isEmpty()) accountCombo.getSelectionModel().selectFirst();

            var cats = new com.ft.app.data.dao.CategoryDao().findAll();
            categoryCombo.getItems().addAll(cats.stream().map(c -> c.name()).toList());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.addRow(0, new Label("Date:"), datePicker);
        grid.addRow(1, new Label("Payee:"), payeeField);
        grid.addRow(2, new Label("Account:"), accountCombo);
        grid.addRow(3, new Label("Category:"), categoryCombo);
        grid.addRow(4, new Label("Amount (e.g., 85.50):"), amountField);
        grid.addRow(5, new Label("Note:"), noteField);

        dlg.getDialogPane().setContent(grid);

        // Basic validation on Save
        var saveBtn = dlg.getDialogPane().lookupButton(saveBtnType);
        saveBtn.addEventFilter(ActionEvent.ACTION, evt -> {
            if (accountCombo.getValue() == null || amountField.getText().isBlank()) {
                evt.consume();
                new Alert(Alert.AlertType.WARNING, "Account and Amount are required.").showAndWait();
            }
        });

        dlg.setResultConverter(bt -> bt == saveBtnType);

        dlg.showAndWait().ifPresent(saved -> {
            if (Boolean.TRUE.equals(saved)) {
                // next step: insert into DB, then refresh table
                trySaveTransaction(
                        datePicker.getValue().toString(),
                        payeeField.getText(),
                        accountCombo.getValue(),
                        categoryCombo.getValue(),
                        amountField.getText(),
                        noteField.getText()
                );
            }
        });
    }

    private void trySaveTransaction(String isoDate, String payee, String accountName,
                                    String categoryName, String amountText, String note) {
        try {
            // parse amount (supports "85.50" or "-32")
            long amountCents = parseAmountToCents(amountText);

            // resolve IDs
            var accDao = new com.ft.app.data.dao.AccountDao();
            var catDao = new com.ft.app.data.dao.CategoryDao();

            Long accountId = accDao.findAll().stream()
                    .filter(a -> a.name().equals(accountName))
                    .map(a -> a.id())
                    .findFirst().orElseThrow();

            Long categoryId = null;
            if (categoryName != null && !categoryName.isBlank()) {
                categoryId = catDao.findAll().stream()
                        .filter(c -> c.name().equals(categoryName))
                        .map(c -> c.id())
                        .findFirst().orElse(null);
            }

            // insert
            var txDao = new com.ft.app.data.dao.TxDao();
            txDao.insert(accountId, categoryId, isoDate, payee, amountCents, note);

            // reload table
            loadData();

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save transaction: " + ex.getMessage()).showAndWait();
        }
    }

    private long parseAmountToCents(String s) {
        s = s.trim().replace(",", "");
        if (s.isBlank()) throw new IllegalArgumentException("Amount required");
        double d = Double.parseDouble(s);                // acceptable here; storage is long cents
        long cents = Math.round(d * 100.0);
        return cents;
    }

}
