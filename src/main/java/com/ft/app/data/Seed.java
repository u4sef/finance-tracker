package com.ft.app.data;

import com.ft.app.data.dao.AccountDao;
import com.ft.app.data.dao.CategoryDao;

/**
 * Seeds initial data on first run.
 */
public final class Seed {
    private Seed() {}

    public static void run() {
        try {
            new AccountDao().seedIfEmpty();
            new CategoryDao().seedIfEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Seeding failed", e);
        }
    }
}
