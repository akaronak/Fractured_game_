package com.fractured.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    private static final String URL = "jdbc:sqlite:fractured.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void initializeSchema() {
        String sql = """
            CREATE TABLE IF NOT EXISTS saves (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                save_name TEXT NOT NULL,
                player_name TEXT,
                current_scene TEXT,
                inventory TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✔ Database initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
