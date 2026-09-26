package com.codecanvas.database;

import com.codecanvas.model.QuizResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAO {

    public void insertResult(QuizResult result) {
        String sql = "INSERT INTO quiz_results (run_id, algorithm, score, total_questions, quiz_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (result.getRunId() != null) {
                stmt.setInt(1, result.getRunId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, result.getAlgorithm());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalQuestions());
            stmt.setString(5, result.getQuizDate());

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

    public int[] getQuizTotalsForAlgorithm(String algorithm) {
        String sql = "SELECT SUM(score) as s, SUM(total_questions) as t FROM quiz_results WHERE algorithm = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new int[]{rs.getInt("s"), rs.getInt("t")};
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new int[]{0, 0};
    }
}