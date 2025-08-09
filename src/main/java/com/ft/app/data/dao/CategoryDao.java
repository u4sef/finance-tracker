package com.ft.app.data.dao;

import com.ft.app.data.Db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the 'category' table.
 */
public class CategoryDao {

    public record Category(long id, String name, String kind) {
    }

    /**
     * Seeds the database with starter categories if empty.
     */
    public void seedIfEmpty() throws SQLException {
        var c = Db.get();
        try (var st = c.createStatement()) {
            var rs = st.executeQuery("SELECT COUNT(*) FROM category");
            rs.next();
            if (rs.getInt(1) == 0) {
                try (var ps = c.prepareStatement(
                        "INSERT INTO category(name, kind) VALUES (?, ?)")) {
                    // Income
                    insert(ps, "Salary", "INCOME");
                    insert(ps, "Interest", "INCOME");
                    // Expense
                    insert(ps, "Rent", "EXPENSE");
                    insert(ps, "Groceries", "EXPENSE");
                    insert(ps, "Dining", "EXPENSE");
                    insert(ps, "Transport", "EXPENSE");
                    insert(ps, "Utilities", "EXPENSE");
                    insert(ps, "Subscriptions", "EXPENSE");
                    insert(ps, "Misc", "EXPENSE");
                }
                c.commit();
            }
        }
    }

    private void insert(PreparedStatement ps, String name, String kind) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, kind);
        ps.executeUpdate();
    }

    public List<Category> findAll() throws SQLException {
        var c = Db.get();
        try (var ps = c.prepareStatement(
                "SELECT id, name, kind FROM category ORDER BY kind, name")) {
            var rs = ps.executeQuery();
            var list = new ArrayList<Category>();
            while (rs.next()) {
                list.add(new Category(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("kind")
                ));
            }
            return list;
        }
    }
}