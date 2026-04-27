package com.assessment.dao;

import com.assessment.model.Attempt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AttemptDAO - Data Access Object for Attempt entity
 * 
 * OOP Concepts:
 * - Inheritance: Extends BaseDAO<Attempt>
 * - Method Overriding: Implements all abstract methods
 */
public class AttemptDAO extends BaseDAO<Attempt> {

    @Override
    public Attempt findById(int id) throws SQLException {
        String sql = "SELECT * FROM attempts WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAttempt(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Attempt> findAll() throws SQLException {
        String sql = "SELECT * FROM attempts ORDER BY attempted_at DESC";
        List<Attempt> attempts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                attempts.add(mapResultSetToAttempt(rs));
            }
            return attempts;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public int insert(Attempt attempt) throws SQLException {
        String sql = "INSERT INTO attempts (session_id, question_id, selected_option, "
                   + "is_correct, difficulty_at_time) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, attempt.getSessionId());
            stmt.setInt(2, attempt.getQuestionId());
            stmt.setString(3, attempt.getSelectedOption());
            stmt.setBoolean(4, attempt.isCorrect());
            stmt.setInt(5, attempt.getDifficultyAtTime());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } finally {
            closeResources(conn, stmt);
        }
    }

    @Override
    public boolean update(Attempt attempt) throws SQLException {
        String sql = "UPDATE attempts SET selected_option = ?, is_correct = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, attempt.getSelectedOption());
            stmt.setBoolean(2, attempt.isCorrect());
            stmt.setInt(3, attempt.getId());

            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM attempts WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt);
        }
    }

    // ---- Additional Query Methods ----

    /**
     * Find all attempts for a given session
     */
    public List<Attempt> findBySessionId(int sessionId) throws SQLException {
        String sql = "SELECT * FROM attempts WHERE session_id = ? ORDER BY attempted_at ASC";
        List<Attempt> attempts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, sessionId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                attempts.add(mapResultSetToAttempt(rs));
            }
            return attempts;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // ---- Private Helper ----

    private Attempt mapResultSetToAttempt(ResultSet rs) throws SQLException {
        return new Attempt(
            rs.getInt("id"),
            rs.getInt("session_id"),
            rs.getInt("question_id"),
            rs.getString("selected_option"),
            rs.getBoolean("is_correct"),
            rs.getInt("difficulty_at_time"),
            rs.getTimestamp("attempted_at")
        );
    }
}
