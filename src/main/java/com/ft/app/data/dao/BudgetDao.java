package com.ft.app.data.dao;

import com.ft.app.data.Db;
import java.sql.SQLException;
import java.util.List;

public class BudgetDao {

    public record BudgetRow(long id, long categoryId, String categoryName,
                            String month, long limitCents, long spentCents) {}

    public List<BudgetRow> listForMonth(String yyyyMm) throws SQLException {
        throw new UnsupportedOperationException("TODO");
    }

    public void upsert(String yyyyMm, long categoryId, long limitCents) throws SQLException {
        throw new UnsupportedOperationException("TODO");
    }
}
