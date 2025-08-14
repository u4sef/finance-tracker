package com.ft.app.ui.budgets;

import com.ft.app.data.dao.BudgetDao;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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
        // will implement after DAO — lets us upsert a budget limit for selected category/month
        new Alert(Alert.AlertType.INFORMATION, "Set/Update coming next.").showAndWait();
    }
}
