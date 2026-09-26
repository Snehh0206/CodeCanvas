package com.codecanvas.database;

import com.codecanvas.model.Run;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RunDAO {

    public void insertRun(Run run) {
        String sql = "INSERT INTO runs (algorithm, case_type, input_size, steps, comparisons, swaps, execution_time_micros, run_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, run.getAlgorithm());
            stmt.setString(2, run.getCaseType());
            stmt.setInt(3, run.getInputSize());
            stmt.setInt(4, run.getSteps());
            stmt.setInt(5, run.getComparisons());
            stmt.setInt(6, run.getSwaps());
            stmt.setLong(7, run.getExecutionTimeMicros());
            stmt.setString(8, run.getRunDate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Run> getAllRuns() {
        List<Run> runs = new ArrayList<>();
        String sql = "SELECT * FROM runs ORDER BY id DESC";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                runs.add(mapRow(rs));
            }
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
        String sql = "SELECT * FROM runs WHERE algorithm = ? ORDER BY input_size ASC";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, algorithm);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                runs.add(mapRow(rs));
            }
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

    public int countRunsForAlgorithm(String algorithm) {
        String sql = "SELECT COUNT(*) as cnt FROM runs WHERE algorithm = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, algorithm);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    

}