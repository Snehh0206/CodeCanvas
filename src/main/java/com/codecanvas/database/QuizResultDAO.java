package com.codecanvas.database;

import com.codecanvas.model.QuizResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAO {

    public void insertResult(QuizResult result) {
        String sql = "INSERT INTO quiz_results (user_id, run_id, algorithm, score, total_questions, quiz_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            if (result.getRunId() != null) stmt.setInt(2, result.getRunId());
            else stmt.setNull(2, java.sql.Types.INTEGER);
            stmt.setString(3, result.getAlgorithm());
            stmt.setInt(4, result.getScore());
            stmt.setInt(5, result.getTotalQuestions());
            stmt.setString(6, result.getQuizDate());
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
        String sql = "SELECT SUM(score) as s, SUM(total_questions) as t FROM quiz_results WHERE algorithm = ? AND user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            stmt.setInt(2, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new int[]{rs.getInt("s"), rs.getInt("t")};
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0};
    }public int[] getOverallQuizScoreForUser() {
        String sql = "SELECT SUM(score) as s, SUM(total_questions) as t FROM quiz_results WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new int[]{rs.getInt("s"), rs.getInt("t")};
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0};
    }
}