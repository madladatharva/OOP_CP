package com.assessment.dao;

import com.assessment.util.DBConnection;
import java.sql.*;
import java.util.List;

/**
 * BaseDAO Abstract Class - Demonstrates ABSTRACTION and INHERITANCE
 * 
 * OOP Concepts:
 * - Abstraction: Abstract class with abstract methods that subclasses must
 * implement
 * - Inheritance: Concrete DAOs extend this base class
 * - Encapsulation: Protected connection management methods
 * 
 * @param <T> The entity type this DAO manages
 */
public abstract class BaseDAO<T> {

    // ---- Abstract Methods (Abstraction) ----
    // Subclasses MUST implement these methods

    /**
     * Find an entity by its ID
     * 
     * @param id Entity ID
     * @return Entity object or null
     */
    public abstract T findById(int id) throws SQLException;

    /**
     * Get all entities
     * 
     * @return List of all entities
     */
    public abstract List<T> findAll() throws SQLException;

    /**
     * Insert a new entity
     * 
     * @param entity Entity to insert
     * @return Generated ID
     */
    public abstract int insert(T entity) throws SQLException;

    /**
     * Update an existing entity
     * 
     * @param entity Entity with updated values
     * @return true if successful
     */
    public abstract boolean update(T entity) throws SQLException;

    /**
     * Delete an entity by ID
     * 
     * @param id Entity ID
     * @return true if successful
     */
    public abstract boolean delete(int id) throws SQLException;

    // ---- Protected Helper Methods (Inherited by subclasses) ----

    /**
     * Get a database connection
     * 
     * @return Connection object
     */
    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    /**
     * Safely close database resources
     */
    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        DBConnection.closeResources(conn, stmt, rs);
    }

    /**
     * Safely close connection and statement (without ResultSet)
     */
    protected void closeResources(Connection conn, PreparedStatement stmt) {
        closeResources(conn, stmt, null);
    }
}
