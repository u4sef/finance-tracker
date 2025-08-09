package com.ft.app.ui.transactions;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/** View-model for one row in the transactions table. */
public class TransactionVm {
    private static final NumberFormat CAD = NumberFormat.getCurrencyInstance(Locale.CANADA);

    private final long id;
    private final LocalDate date;
    private final String payee;
    private final String category;
    private final String account;
    private final long amountCents;
    private final String note;

    public TransactionVm(long id, String isoDate, String payee, String category,
                         String account, long amountCents, String note) {
        this.id = id;
        this.date = LocalDate.parse(isoDate);
        this.payee = payee == null ? "" : payee;
        this.category = category == null ? "—" : category;
        this.account = account;
        this.amountCents = amountCents;
        this.note = note == null ? "" : note;
    }

    // Getters for JavaFX PropertyValueFactory (must be named getX)
    public long getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getPayee() { return payee; }
    public String getCategory() { return category; }
    public String getAccount() { return account; }
    public String getAmount() { return CAD.format(amountCents / 100.0); }
    public String getNote() { return note; }
}
