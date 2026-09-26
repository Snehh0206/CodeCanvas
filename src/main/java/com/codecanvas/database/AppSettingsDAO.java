package com.codecanvas.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppSettingsDAO {

    public String[] getSettings() {
        String sql = "SELECT theme, quiz_difficulty, default_speed FROM settings WHERE id = 1";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new String[]{
                        rs.getString("theme"),
                        rs.getString("quiz_difficulty"),
                        String.valueOf(rs.getInt("default_speed"))
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{"light", "medium", "5"};
    }

    public void updateSettings(String theme, String difficulty, int speed) {
        String sql = "UPDATE settings SET theme = ?, quiz_difficulty = ?, default_speed = ? WHERE id = 1";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, theme);
            stmt.setString(2, difficulty);
            stmt.setInt(3, speed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}