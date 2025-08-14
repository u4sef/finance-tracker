package com.ft.app.data.dao;

import com.ft.app.data.Db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for budget limits and monthly spend.
 */
public class BudgetDao {

    public record BudgetRow(long id, long categoryId, String categoryName,
                            String month, long limitCents, long spentCents) {}

    /**
     * Returns budget rows for a given YYYY-MM. If a category has no limit set,
     * it will not appear unless you extend this to LEFT JOIN over all EXPENSE categories.
     */
    public List<BudgetRow> listForMonth(String yyyyMm) throws SQLException {
        var c = Db.get();
        try (var ps = c.prepareStatement("""
            SELECT b.id, b.category_id, ca.name AS category_name, b.month, b.amount_cents AS limit_cents,
                   COALESCE((
                     SELECT ABS(SUM(t.amount_cents))
                     FROM tx t
                     JOIN category c2 ON t.category_id = c2.id
                     WHERE t.category_id = b.category_id
                       AND c2.kind = 'EXPENSE'
                       AND substr(t.dt,1,7) = b.month
                   ), 0) AS spent_cents
            FROM budget b
            JOIN category ca ON ca.id = b.category_id
            WHERE b.month = ?
            ORDER BY ca.name
        """)) {
            ps.setString(1, yyyyMm);
            var rs = ps.executeQuery();
            var list = new ArrayList<BudgetRow>();
            while (rs.next()) {
                list.add(new BudgetRow(
                        rs.getLong("id"),
                        rs.getLong("category_id"),
                        rs.getString("category_name"),
                        rs.getString("month"),
                        rs.getLong("limit_cents"),
                        rs.getLong("spent_cents")
                ));
            }
            return list;
        }
    }

    /**
     * Create or update a budget limit for a single category & month (YYYY-MM).
     */
    public void upsert(String yyyyMm, long categoryId, long limitCents) throws SQLException {
        var c = Db.get();
        // Try update, if 0 rows updated → insert
        try (var upd = c.prepareStatement("""
            UPDATE budget SET amount_cents = ?
            WHERE category_id = ? AND month = ?
        """)) {
            upd.setLong(1, limitCents);
            upd.setLong(2, categoryId);
            upd.setString(3, yyyyMm);
            int changed = upd.executeUpdate();
            if (changed == 0) {
                try (var ins = c.prepareStatement("""
                    INSERT INTO budget(category_id, month, amount_cents) VALUES (?,?,?)
                """)) {
                    ins.setLong(1, categoryId);
                    ins.setString(2, yyyyMm);
                    ins.setLong(3, limitCents);
                    ins.executeUpdate();
                }
            }
            c.commit();
        }
    }
}
