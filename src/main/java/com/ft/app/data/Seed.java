package com.ft.app.data;

import com.ft.app.data.dao.AccountDao;
import com.ft.app.data.dao.CategoryDao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Seeds initial data on first run.
 */
public final class Seed {
    private Seed() {}

    public static void run() {
        try {
            new AccountDao().seedIfEmpty();
            new CategoryDao().seedIfEmpty();
            seedSampleTransactions(); // <-- add a few demo rows for the visual MVP
        } catch (Exception e) {
            throw new RuntimeException("Seeding failed", e);
        }
    }

    private static void seedSampleTransactions() throws SQLException {
        var c = Db.get();

        // If there are already transactions, don't add demo data
        try (var st = c.createStatement()) {
            var rs = st.executeQuery("SELECT COUNT(*) FROM tx");
            rs.next();
            if (rs.getInt(1) > 0) return;
        }

        // Look up needed IDs by name
        long accCheq     = findId("SELECT id FROM account WHERE name = ?", "Chequing");
        long accCredit   = findId("SELECT id FROM account WHERE name = ?", "Credit");
        long catSalary   = findId("SELECT id FROM category WHERE name = ?", "Salary");
        long catRent     = findId("SELECT id FROM category WHERE name = ?", "Rent");
        long catGroceries= findId("SELECT id FROM category WHERE name = ?", "Groceries");
        long catTransport= findId("SELECT id FROM category WHERE name = ?", "Transport");

        // Insert a few realistic transactions (amounts are in cents; expenses are negative)
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tx(account_id, category_id, dt, payee, amount_cents, note) VALUES (?,?,?,?,?,?)")) {

            insertTx(ps, accCheq,   catSalary,    "2025-08-01", "ACME Corp Payroll",   500_000, "August salary");    // +$5,000.00
            insertTx(ps, accCheq,   catRent,      "2025-08-02", "Landlord",          -150_000, "August rent");       // -$1,500.00
            insertTx(ps, accCredit, catGroceries, "2025-08-03", "Metro",               -8_500, "Weekly groceries");  // -$85.00
            insertTx(ps, accCredit, catTransport, "2025-08-04", "Uber",                 -3_200, "Ride to work");     // -$32.00
        }

        c.commit();
    }

    private static long findId(String sql, String name) throws SQLException {
        var c = Db.get();
        try (var ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
            throw new SQLException("Missing seed dependency: " + name);
        }
    }

    private static void insertTx(PreparedStatement ps, long accountId, long categoryId,
                                 String dt, String payee, long amountCents, String note) throws SQLException {
        ps.setLong(1, accountId);
        ps.setLong(2, categoryId);
        ps.setString(3, dt);
        ps.setString(4, payee);
        ps.setLong(5, amountCents);
        ps.setString(6, note);
        ps.executeUpdate();
    }
}
