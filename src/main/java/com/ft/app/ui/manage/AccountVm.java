package com.ft.app.ui.manage;

public class AccountVm {
    private final long id;
    private final String name;
    private final String type;
    private final String currency;

    public AccountVm(long id, String name, String type, String currency) {
        this.id = id; this.name = name; this.type = type; this.currency = currency;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCurrency() { return currency; }
}
