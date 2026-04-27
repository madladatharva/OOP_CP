package com.assessment.dao;

import com.assessment.model.QuizSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizSessionDAO - Data Access Object for QuizSession entity
 * 
 * OOP Concepts:
 * - Inheritance: Extends BaseDAO<QuizSession>
 * - Method Overriding: Implements all abstract methods
 */
public class QuizSessionDAO extends BaseDAO<QuizSession> {

    @Override
    public QuizSession findById(int id) throws SQLException {
        String sql = "SELECT * FROM quiz_sessions WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<QuizSession> findAll() throws SQLException {
        String sql = "SELECT * FROM quiz_sessions ORDER BY started_at DESC";
        List<QuizSession> sessions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            return sessions;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public int insert(QuizSession session) throws SQLException {
        String sql = "INSERT INTO quiz_sessions (user_id, current_difficulty, total_questions, "
                   + "selected_category, current_question_number, score, consecutive_correct, "
                   + "consecutive_wrong, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, session.getUserId());
            stmt.setInt(2, session.getCurrentDifficulty());
            stmt.setInt(3, session.getTotalQuestions());
            stmt.setString(4, session.getSelectedCategory());
            stmt.setInt(5, session.getCurrentQuestionNumber());
            stmt.setInt(6, session.getScore());
            stmt.setInt(7, session.getConsecutiveCorrect());
            stmt.setInt(8, session.getConsecutiveWrong());
            stmt.setString(9, session.getStatus());

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
    public boolean update(QuizSession session) throws SQLException {
        String sql = "UPDATE quiz_sessions SET current_difficulty = ?, "
                   + "current_question_number = ?, score = ?, "
                   + "consecutive_correct = ?, consecutive_wrong = ?, "
                   + "status = ?, ended_at = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, session.getCurrentDifficulty());
            stmt.setInt(2, session.getCurrentQuestionNumber());
            stmt.setInt(3, session.getScore());
            stmt.setInt(4, session.getConsecutiveCorrect());
            stmt.setInt(5, session.getConsecutiveWrong());
            stmt.setString(6, session.getStatus());
            stmt.setTimestamp(7, session.getEndedAt());
            stmt.setInt(8, session.getId());

            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM quiz_sessions WHERE id = ?";
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
     * Find all sessions for a given user
     */
    public List<QuizSession> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quiz_sessions WHERE user_id = ? ORDER BY started_at DESC";
        List<QuizSession> sessions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            return sessions;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Find active session for user (if any)
     */
    public QuizSession findActiveSession(int userId) throws SQLException {
        String sql = "SELECT * FROM quiz_sessions WHERE user_id = ? AND status = 'ACTIVE' "
                   + "ORDER BY started_at DESC LIMIT 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // ---- Private Helper ----

    private QuizSession mapResultSetToSession(ResultSet rs) throws SQLException {
        return new QuizSession(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("current_difficulty"),
            rs.getInt("total_questions"),
            rs.getString("selected_category"),
            rs.getInt("current_question_number"),
            rs.getInt("score"),
            rs.getInt("consecutive_correct"),
            rs.getInt("consecutive_wrong"),
            rs.getString("status"),
            rs.getTimestamp("started_at"),
            rs.getTimestamp("ended_at")
        );
    }
}
