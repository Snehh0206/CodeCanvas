package com.codecanvas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:codecanvas.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createRunsTable = """
            CREATE TABLE IF NOT EXISTS runs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                algorithm TEXT,
                case_type TEXT,
                input_size INTEGER,
                steps INTEGER,
                comparisons INTEGER,
                swaps INTEGER,
                execution_time_micros INTEGER,
                run_date TEXT
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createRunsTable);
            System.out.println("Database ready: runs table exists");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}