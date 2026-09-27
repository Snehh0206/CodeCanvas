package com.codecanvas.database;

import com.codecanvas.model.Run;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunDAO {

    public int insertRun(Run run) {
        String sql = "INSERT INTO runs (user_id, algorithm, case_type, input_size, steps, comparisons, swaps, execution_time_micros, run_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            stmt.setString(2, run.getAlgorithm());
            stmt.setString(3, run.getCaseType());
            stmt.setInt(4, run.getInputSize());
            stmt.setInt(5, run.getSteps());
            stmt.setInt(6, run.getComparisons());
            stmt.setInt(7, run.getSwaps());
            stmt.setLong(8, run.getExecutionTimeMicros());
            stmt.setString(9, run.getRunDate());
            stmt.setString(10, run.getNotes());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Run> getAllRuns() {
        List<Run> runs = new ArrayList<>();
        String sql = "SELECT * FROM runs WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) runs.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return runs;
    }

    public List<Run> getRunsByAlgorithmAndCase(String algorithm, String caseType) {
        List<Run> runs = new ArrayList<>();
        String sql = "SELECT * FROM runs WHERE algorithm = ? AND case_type = ? ORDER BY id DESC";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, algorithm);
            stmt.setString(2, caseType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                runs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return runs;
    }

    public List<Run> getRunsByAlgorithm(String algorithm) {
        List<Run> runs = new ArrayList<>();
        String sql = "SELECT * FROM runs WHERE algorithm = ? AND user_id = ? ORDER BY input_size ASC";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            stmt.setInt(2, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) runs.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return runs;
    }

    public void deleteRun(int id) {
        String sql = "DELETE FROM runs WHERE id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Run mapRow(ResultSet rs) throws SQLException {
        return new Run(
                rs.getInt("id"),
                rs.getString("algorithm"),
                rs.getString("case_type"),
                rs.getInt("input_size"),
                rs.getInt("steps"),
                rs.getInt("comparisons"),
                rs.getInt("swaps"),
                rs.getLong("execution_time_micros"),
                rs.getString("run_date")
        );
    }

    public void updateNotes(int runId, String notes) {
        String sql = "UPDATE runs SET notes = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notes);
            stmt.setInt(2, runId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countRunsForAlgorithm(String algorithm) {
        String sql = "SELECT COUNT(*) as cnt FROM runs WHERE algorithm = ? AND user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            stmt.setInt(2, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAllRunsForUser() {
        String sql = "SELECT COUNT(*) as cnt FROM runs WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getMostRecentAlgorithm() {
        String sql = "SELECT algorithm FROM runs WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, com.codecanvas.model.UserSession.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("algorithm");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "None yet";
    }

}