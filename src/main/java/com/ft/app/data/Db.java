package com.ft.app.data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Handles connecting to the SQLite database and bootstrapping the schema.
 */
public final class Db {
    private static final String APP_DIR = System.getenv("LOCALAPPDATA") + "\\Ledger";
    private static final String DB_PATH = APP_DIR + "\\ledger.db";
    private static Connection conn;

    private Db() {}

    /**
     * Returns the single DB connection, opening and creating schema if needed.
     */
    public static synchronized Connection get() {
        if (conn != null) return conn;
        try {
            // Make sure folder exists
            Files.createDirectories(Paths.get(APP_DIR));

            // Connect to SQLite file
            String url = "jdbc:sqlite:" + DB_PATH;
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);

            // Create tables if they dont exist
            initSchema(conn);

            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Failed to open database", e);
        }
    }

    /**
     * Creates all necessary tables if they don't exist.
     */
    private static void initSchema(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.addBatch("""
                CREATE TABLE IF NOT EXISTS account(
                  id INTEGER PRIMARY KEY,
                  name TEXT NOT NULL,
                  type TEXT NOT NULL,      -- CHECKING, SAVINGS, CASH, CREDIT
                  currency TEXT NOT NULL
                );
            """);
            st.addBatch("""
                CREATE TABLE IF NOT EXISTS category(
                  id INTEGER PRIMARY KEY,
                  name TEXT NOT NULL,
                  kind TEXT NOT NULL       -- INCOME or EXPENSE
                );
            """);
            st.addBatch("""
                CREATE TABLE IF NOT EXISTS tx(
                  id INTEGER PRIMARY KEY,
                  account_id INTEGER NOT NULL,
                  category_id INTEGER,
                  dt TEXT NOT NULL,        -- ISO date YYYY-MM-DD
                  payee TEXT,
                  amount_cents INTEGER NOT NULL,
                  note TEXT,
                  FOREIGN KEY(account_id) REFERENCES account(id),
                  FOREIGN KEY(category_id) REFERENCES category(id)
                );
            """);
            st.addBatch("""
                CREATE TABLE IF NOT EXISTS budget(
                  id INTEGER PRIMARY KEY,
                  category_id INTEGER NOT NULL,
                  month TEXT NOT NULL,     -- YYYY-MM
                  amount_cents INTEGER NOT NULL,
                  UNIQUE(category_id, month),
                  FOREIGN KEY(category_id) REFERENCES category(id)
                );
            """);
            st.executeBatch();
            c.commit();
        }
    }

    /**
     * Safely close the DB connection.
     */
    public static void closeQuietly() {
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        conn = null;
    }
}
