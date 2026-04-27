package com.assessment.dao;

import com.assessment.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User entity
 * 
 * OOP Concepts:
 * - Inheritance: Extends BaseDAO<User>
 * - Method Overriding: Implements all abstract methods from BaseDAO
 * - Polymorphism: Can be used as BaseDAO<User> reference
 */
public class UserDAO extends BaseDAO<User> {

    // ---- Implementing Abstract Methods (Method Overriding) ----

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole());

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
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, full_name = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getId());

            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
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

    // ---- Additional DAO Methods ----

    /**
     * Authenticate user by username and password
     * 
     * @return User object if valid, null otherwise
     */
    public User authenticate(String username, String password) throws SQLException {
        // Find user by username first to get the hashed password
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                // For backward compatibility: Check if stored password is plain text or hash
                boolean isMatch;
                if (storedHash.startsWith("$2a$")) {
                    isMatch = org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHash);
                } else {
                    isMatch = storedHash.equals(password);
                    // In a real system, you'd auto-upgrade the pass here
                }

                if (isMatch) {
                    return mapResultSetToUser(rs);
                }
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Check if username already exists
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // ---- Private Helper Method ----

    /**
     * Maps a ResultSet row to a User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getTimestamp("created_at"));
    }
}
