package com.ft.app.ui.manage;

public class CategoryVm {
    private final long id;
    private final String name;
    private final String kind;

    public CategoryVm(long id, String name, String kind) {
        this.id = id; this.name = name; this.kind = kind;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getKind() { return kind; }
}
