package com.ft.app.ui.budgets;

import com.ft.app.data.dao.BudgetDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class BudgetsView extends BorderPane {
    private final TableView<BudgetVm> table = new TableView<>();
    private final ComboBox<String> monthCombo = new ComboBox<>();
    private final Label status = new Label();
    private final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

    public BudgetsView() {
        setTop(buildHeader());
        setCenter(buildTable());
        setBottom(new ToolBar(status));

        // default to current month
        monthCombo.getItems().addAll(
                LocalDate.now().minusMonths(1).format(YM),
                LocalDate.now().format(YM),
                LocalDate.now().plusMonths(1).format(YM)
        );
        monthCombo.getSelectionModel().select(LocalDate.now().format(YM));

        monthCombo.setOnAction(e -> load());
        load();
    }

    private Node buildHeader() {
        var title = new Label("Budgets");
        title.getStyleClass().add("h2");

        var addBtn = new Button("Set/Update Limit");
        addBtn.setOnAction(e -> openSetLimitDialog());

        return new ToolBar(title, new Separator(), new Label("Month:"), monthCombo, new Separator(), addBtn);
    }

    private Node buildTable() {
        var colCat = new TableColumn<BudgetVm, String>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        var colLimit = new TableColumn<BudgetVm, String>("Limit");
        colLimit.setCellValueFactory(new PropertyValueFactory<>("limit"));

        var colSpent = new TableColumn<BudgetVm, String>("Spent");
        colSpent.setCellValueFactory(new PropertyValueFactory<>("spent"));

        var colRemain = new TableColumn<BudgetVm, String>("Remaining");
        colRemain.setCellValueFactory(new PropertyValueFactory<>("remaining"));

        var colProg = new TableColumn<BudgetVm, Double>("Progress");
        colProg.setCellValueFactory(new PropertyValueFactory<>("progress"));
        colProg.setCellFactory(col -> {
            return new TableCell<>() {
                private final ProgressBar bar = new ProgressBar(0);
                {
                    setAlignment(Pos.CENTER);
                    bar.setPrefWidth(160);
                }
                @Override protected void updateItem(Double val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty || val == null) { setGraphic(null); setText(null); return; }
                    bar.setProgress(val);
                    setGraphic(bar);
                }
            };
        });

        table.getColumns().addAll(colCat, colLimit, colSpent, colRemain, colProg);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No budgets set"));

        return new StackPane(table);
    }

    private void load() {
        try {
            var rows = new BudgetDao().listForMonth(monthCombo.getValue());
            var vms = rows.stream().map(r ->
                            new BudgetVm(r.id(), r.categoryName(), r.month(), r.limitCents(), r.spentCents()))
                    .collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(vms));
            status.setText(vms.size() + " categories");
        } catch (Exception ex) {
            ex.printStackTrace();
            status.setText("Failed to load budgets");
        }
    }

    private void openSetLimitDialog() {
        var dlg = new Dialog<Boolean>();
        dlg.setTitle("Set/Update Budget Limit");

        var saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        var catCombo = new ComboBox<String>();
        var limitField = new TextField();

        // Load EXPENSE categories only
        try {
            var all = new com.ft.app.data.dao.CategoryDao().findAll();
            var expenseNames = all.stream()
                    .filter(c -> "EXPENSE".equalsIgnoreCase(c.kind()))
                    .map(c -> c.name())
                    .sorted()
                    .toList();
            catCombo.getItems().addAll(expenseNames);
            if (!expenseNames.isEmpty()) catCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.addRow(0, new Label("Month:"), new Label(monthCombo.getValue()));
        grid.addRow(1, new Label("Category:"), catCombo);
        grid.addRow(2, new Label("Limit (e.g., 400.00):"), limitField);

        dlg.getDialogPane().setContent(grid);

        // Simple validation
        var saveBtn = dlg.getDialogPane().lookupButton(saveType);
        saveBtn.addEventFilter(ActionEvent.ACTION, evt -> {
            if (catCombo.getValue() == null || limitField.getText().isBlank()) {
                evt.consume();
                new Alert(Alert.AlertType.WARNING, "Category and Limit are required.").showAndWait();
            } else {
                try { parseAmountToCents(limitField.getText()); }
                catch (Exception ex) {
                    evt.consume();
                    new Alert(Alert.AlertType.WARNING, "Enter a valid amount like 400 or 400.00").showAndWait();
                }
            }
        });

        dlg.setResultConverter(bt -> bt == saveType);
        dlg.showAndWait().ifPresent(ok -> {
            if (Boolean.TRUE.equals(ok)) {
                try {
                    var month = monthCombo.getValue();
                    var limitCents = parseAmountToCents(limitField.getText());

                    // Resolve category id by name
                    var catDao = new com.ft.app.data.dao.CategoryDao();
                    Long categoryId = catDao.findAll().stream()
                            .filter(c -> c.name().equals(catCombo.getValue()))
                            .map(c -> c.id())
                            .findFirst()
                            .orElseThrow();

                    new com.ft.app.data.dao.BudgetDao().upsert(month, categoryId, limitCents);
                    load(); // refresh table
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to save budget: " + ex.getMessage()).showAndWait();
                }
            }
        });
    }

    // helper
    private long parseAmountToCents(String s) {
        s = s.trim().replace(",", "");
        double d = Double.parseDouble(s);
        return Math.round(d * 100.0);
    }

}
