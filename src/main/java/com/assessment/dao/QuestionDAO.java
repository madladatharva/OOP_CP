package com.assessment.dao;

import com.assessment.model.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuestionDAO - Data Access Object for Question entity
 * 
 * OOP Concepts:
 * - Inheritance: Extends BaseDAO<Question>
 * - Method Overriding: Implements all abstract methods
 */
public class QuestionDAO extends BaseDAO<Question> {

    @Override
    public Question findById(int id) throws SQLException {
        String sql = "SELECT * FROM questions WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuestion(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<Question> findAll() throws SQLException {
        String sql = "SELECT * FROM questions ORDER BY difficulty_level, category";
        List<Question> questions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            return questions;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public int insert(Question question) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return insert(question, conn);
        } finally {
            closeResources(conn, null, null);
        }
    }

    public int insert(Question question, Connection conn) throws SQLException {
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, "
                   + "correct_option, difficulty_level, category, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, question.getQuestionText());
            stmt.setString(2, question.getOptionA());
            stmt.setString(3, question.getOptionB());
            stmt.setString(4, question.getOptionC());
            stmt.setString(5, question.getOptionD());
            stmt.setString(6, question.getCorrectOption());
            stmt.setInt(7, question.getDifficultyLevel());
            stmt.setString(8, question.getCategory());
            stmt.setInt(9, question.getCreatedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    public int insertBatch(List<Question> questions, Connection conn) throws SQLException {
        String sql = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, "
                + "correct_option, difficulty_level, category, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            for (Question question : questions) {
                stmt.setString(1, question.getQuestionText());
                stmt.setString(2, question.getOptionA());
                stmt.setString(3, question.getOptionB());
                stmt.setString(4, question.getOptionC());
                stmt.setString(5, question.getOptionD());
                stmt.setString(6, question.getCorrectOption());
                stmt.setInt(7, question.getDifficultyLevel());
                stmt.setString(8, question.getCategory());
                stmt.setInt(9, question.getCreatedBy());
                stmt.addBatch();
            }

            int[] batchResults = stmt.executeBatch();
            int inserted = 0;
            for (int result : batchResults) {
                if (result >= 0 || result == Statement.SUCCESS_NO_INFO) {
                    inserted++;
                }
            }
            return inserted;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    @Override
    public boolean update(Question question) throws SQLException {
        String sql = "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, "
                   + "option_c = ?, option_d = ?, correct_option = ?, "
                   + "difficulty_level = ?, category = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, question.getQuestionText());
            stmt.setString(2, question.getOptionA());
            stmt.setString(3, question.getOptionB());
            stmt.setString(4, question.getOptionC());
            stmt.setString(5, question.getOptionD());
            stmt.setString(6, question.getCorrectOption());
            stmt.setInt(7, question.getDifficultyLevel());
            stmt.setString(8, question.getCategory());
            stmt.setInt(9, question.getId());

            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
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
     * Find questions by difficulty level
     * @param difficultyLevel 1=Easy, 2=Medium, 3=Hard
     * @return List of matching questions
     */
    public List<Question> findByDifficulty(int difficultyLevel) throws SQLException {
        String sql = "SELECT * FROM questions WHERE difficulty_level = ? ORDER BY RAND()";
        List<Question> questions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, difficultyLevel);
            rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            return questions;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Get a random question of given difficulty that has not been asked in this session
     * @param difficultyLevel Desired difficulty
     * @param sessionId Current session ID (to exclude already asked questions)
     * @return A random Question or null if none available
     */
    public Question getRandomQuestion(int difficultyLevel, int sessionId) throws SQLException {
        return getRandomQuestion(difficultyLevel, sessionId, null);
    }

    public Question getRandomQuestion(int difficultyLevel, int sessionId, String category) throws SQLException {
        String sql = buildRandomQuestionSql(category != null, true);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            int paramIndex = 1;
            stmt.setInt(paramIndex++, difficultyLevel);
            if (category != null) {
                stmt.setString(paramIndex++, category);
            }
            stmt.setInt(paramIndex, sessionId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuestion(rs);
            }

            // If no questions at this difficulty, try adjacent difficulties
            return getRandomQuestionFallback(difficultyLevel, sessionId, category);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Fallback: if no questions at target difficulty, find from any difficulty
     */
    private Question getRandomQuestionFallback(int difficultyLevel, int sessionId, String category)
            throws SQLException {
        String sql = buildRandomQuestionSql(category != null, false);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            if (category != null) {
                stmt.setString(1, category);
                stmt.setInt(2, sessionId);
                stmt.setInt(3, difficultyLevel);
            } else {
                stmt.setInt(1, sessionId);
                stmt.setInt(2, difficultyLevel);
            }
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuestion(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Count total questions in database
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Count questions by difficulty level
     */
    public int countByDifficulty(int difficultyLevel) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE difficulty_level = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, difficultyLevel);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public int countByCategory(String category) throws SQLException {
        if (category == null || category.trim().isEmpty()) {
            return countAll();
        }

        String sql = "SELECT COUNT(*) FROM questions WHERE category = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    public List<String> findAllCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM questions "
                + "WHERE category IS NOT NULL AND category <> '' ORDER BY category";
        List<String> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
            return categories;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // ---- Private Helper ----

    private String buildRandomQuestionSql(boolean filterByCategory, boolean exactDifficulty) {
        StringBuilder sql = new StringBuilder("SELECT q.* FROM questions q WHERE ");
        if (exactDifficulty) {
            sql.append("q.difficulty_level = ? AND ");
        }
        if (filterByCategory) {
            sql.append("q.category = ? AND ");
        }
        sql.append("q.id NOT IN (SELECT a.question_id FROM attempts a WHERE a.session_id = ?) ");
        if (exactDifficulty) {
            sql.append("ORDER BY RAND() LIMIT 1");
        } else {
            sql.append("ORDER BY ABS(q.difficulty_level - ?), RAND() LIMIT 1");
        }
        return sql.toString();
    }

    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("id"),
            rs.getString("question_text"),
            rs.getString("option_a"),
            rs.getString("option_b"),
            rs.getString("option_c"),
            rs.getString("option_d"),
            rs.getString("correct_option"),
            rs.getInt("difficulty_level"),
            rs.getString("category"),
            rs.getInt("created_by"),
            rs.getTimestamp("created_at")
        );
    }
}
