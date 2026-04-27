package com.assessment.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static final String URL = "jdbc:mysql://localhost:3306/smart_assessment_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static HikariDataSource dataSource;

    static {
        try {
            // Register MySQL Driver just in case (optional for newer JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);

            // Connection Pool configurations
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized.");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("HikariDataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.warn("Error closing ResultSet", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.warn("Error closing Statement", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Error closing Connection", e);
            }
        }
    }
}
