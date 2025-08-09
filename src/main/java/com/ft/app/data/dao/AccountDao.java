package com.ft.app.data.dao;

import com.ft.app.data.Db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the 'account' table.
 */
public class AccountDao {

    public record Account(long id, String name, String type, String currency) {
    }

    /**
     * Seeds the database with default accounts if empty.
     */
    public void seedIfEmpty() throws SQLException {
        var c = Db.get();
        try (var st = c.createStatement()) {
            var rs = st.executeQuery("SELECT COUNT(*) FROM account");
            rs.next();
            if (rs.getInt(1) == 0) {
                try (var ps = c.prepareStatement(
                        "INSERT INTO account(name, type, currency) VALUES (?,?,?)")) {
                    insert(ps, "Chequing", "CHECKING", "CAD");
                    insert(ps, "Cash", "CASH", "CAD");
                    insert(ps, "Credit", "CREDIT", "CAD");
                }
                c.commit();
            }
        }
    }

    private void insert(PreparedStatement ps, String name, String type, String currency) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, type);
        ps.setString(3, currency);
        ps.executeUpdate();
    }

    /**
     * Returns all accounts in the database.
     */
    public List<Account> findAll() throws SQLException {
        var c = Db.get();
        try (var ps = c.prepareStatement(
                "SELECT id, name, type, currency FROM account ORDER BY id")) {
            var rs = ps.executeQuery();
            var list = new ArrayList<Account>();
            while (rs.next()) {
                list.add(new Account(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("currency")
                ));
            }
            return list;
        }
    }
}