package com.assessment.service;

import com.assessment.dao.UserDAO;
import com.assessment.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * UserService - Business logic for user operations
 * 
 * Separates business logic from data access and controller layers.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Register a new user
     * 
     * @return registered User with generated ID, or null if failed
     * @throws IllegalArgumentException if validation fails
     */
    public User register(String username, String password, String email, String fullName)
            throws SQLException {
        return register(username, password, email, fullName, "STUDENT");
    }

    public User register(String username, String password, String email, String fullName, String role)
            throws SQLException {

        // Validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (!"TEACHER".equals(role) && !"STUDENT".equals(role)) {
            throw new IllegalArgumentException("Role must be Teacher or Student");
        }

        // Check for duplicates
        if (userDAO.usernameExists(username.trim())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDAO.emailExists(email.trim())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create and insert user with hashed password
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
        User user = new User(username.trim(), hashedPassword, email.trim(), fullName.trim());
        user.setRole(role);
        int generatedId = userDAO.insert(user);

        if (generatedId > 0) {
            user.setId(generatedId);
            return user;
        }
        return null;
    }

    /**
     * Authenticate user login
     * 
     * @return User if valid credentials, null otherwise
     */
    public User login(String username, String password) throws SQLException {
        if (username == null || password == null) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password);
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) throws SQLException {
        return userDAO.findById(id);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }
}
