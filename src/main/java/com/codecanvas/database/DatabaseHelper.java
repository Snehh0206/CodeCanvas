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
            run_date TEXT,
            notes TEXT
        );
    """;

        String createQuizTable = """
        CREATE TABLE IF NOT EXISTS quiz_results (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            run_id INTEGER,
            algorithm TEXT,
            score INTEGER,
            total_questions INTEGER,
            quiz_date TEXT,
            FOREIGN KEY (run_id) REFERENCES runs(id)
        );
    """;

        String createSettingsTable = """
        CREATE TABLE IF NOT EXISTS settings (
            id INTEGER PRIMARY KEY,
            theme TEXT,
            quiz_difficulty TEXT,
            default_speed INTEGER
        );
    """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createRunsTable);
            stmt.execute(createQuizTable);
            stmt.execute(createSettingsTable);

            // Seed the single settings row if it doesn't exist yet
            stmt.execute("INSERT OR IGNORE INTO settings (id, theme, quiz_difficulty, default_speed) VALUES (1, 'light', 'medium', 5)");

            System.out.println("Database ready: runs, quiz_results, settings tables exist");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}