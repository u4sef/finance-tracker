package com.ft.app.ui.budgets;

public class BudgetVm {
    private final long id;
    private final String category;
    private final String month;
    private final long limitCents;
    private final long spentCents;

    public BudgetVm(long id, String category, String month, long limitCents, long spentCents) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.limitCents = limitCents;
        this.spentCents = spentCents;
    }

    public long getId() { return id; }
    public String getCategory() { return category; }
    public String getMonth() { return month; }
    public String getLimit() { return centsToCad(limitCents); }
    public String getSpent() { return centsToCad(spentCents); }
    public String getRemaining() { return centsToCad(limitCents - spentCents); }
    public double getProgress() {
        if (limitCents <= 0) return 0d;
        return Math.max(0d, Math.min(1d, (spentCents * 1.0) / limitCents));
    }

    private static String centsToCad(long cents) {
        return String.format("$%.2f", cents / 100.0);
    }
}
