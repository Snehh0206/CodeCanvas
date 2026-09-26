package com.codecanvas.database;

import com.codecanvas.model.QuizResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAO {

    public void insertResult(QuizResult result) {
        String sql = "INSERT INTO quiz_results (algorithm, score, total_questions, quiz_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, result.getAlgorithm());
            stmt.setInt(2, result.getScore());
            stmt.setInt(3, result.getTotalQuestions());
            stmt.setString(4, result.getQuizDate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<QuizResult> getAllResults() {
        List<QuizResult> results = new ArrayList<>();
        String sql = "SELECT * FROM quiz_results ORDER BY id DESC";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(new QuizResult(
                        rs.getInt("id"),
                        rs.getString("algorithm"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getString("quiz_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}