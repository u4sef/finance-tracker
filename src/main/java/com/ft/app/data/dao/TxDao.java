package com.ft.app.data.dao;

import com.ft.app.data.Db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Read-only queries for the tx (transactions) table. */
public class TxDao {
    public record TxRow(
            long id,
            String dt,           // YYYY-MM-DD
            String payee,
            long amountCents,
            String note,
            String accountName,
            String categoryName
    ) {}

    /** Returns all transactions, newest first. */
    public List<TxRow> listAll() throws SQLException {
        var c = Db.get();
        try (var ps = c.prepareStatement("""
            SELECT t.id, t.dt, t.payee, t.amount_cents, t.note,
                   a.name AS account_name,
                   IFNULL(ca.name, '—') AS category_name
            FROM tx t
            JOIN account a ON t.account_id = a.id
            LEFT JOIN category ca ON t.category_id = ca.id
            ORDER BY t.dt DESC, t.id DESC
        """)) {
            var rs = ps.executeQuery();
            var list = new ArrayList<TxRow>();
            while (rs.next()) {
                list.add(new TxRow(
                        rs.getLong("id"),
                        rs.getString("dt"),
                        rs.getString("payee"),
                        rs.getLong("amount_cents"),
                        rs.getString("note"),
                        rs.getString("account_name"),
                        rs.getString("category_name")
                ));
            }
            return list;
        }
    }

    public long insert(long accountId, Long categoryIdOrNull, String isoDate,
                       String payee, long amountCents, String note) throws SQLException {
        var c = com.ft.app.data.Db.get();
        try (var ps = c.prepareStatement("""
        INSERT INTO tx(account_id, category_id, dt, payee, amount_cents, note)
        VALUES (?, ?, ?, ?, ?, ?)
    """, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, accountId);
            if (categoryIdOrNull == null) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setLong(2, categoryIdOrNull);
            ps.setString(3, isoDate);
            ps.setString(4, payee);
            ps.setLong(5, amountCents);
            ps.setString(6, note);
            ps.executeUpdate();
            var keys = ps.getGeneratedKeys();
            long id = keys.next() ? keys.getLong(1) : -1;
            c.commit();
            return id;
        }
    }

}
