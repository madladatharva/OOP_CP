package com.assessment.model;

import java.sql.Timestamp;

/**
 * User Model Class - Demonstrates ENCAPSULATION and CONSTRUCTOR OVERLOADING
 * 
 * OOP Concepts:
 * - Encapsulation: All fields are private with public getters/setters
 * - Constructor Overloading: Multiple constructors for different scenarios
 */
public class User {

    // ---- Private Fields (Encapsulation) ----
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private Timestamp createdAt;

    // ---- Constructor Overloading ----

    /** Default constructor */
    public User() {
        this.role = "STUDENT";
    }

    /** Constructor for login (username + password only) */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "STUDENT";
    }

    /** Constructor for registration (without id) */
    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = "STUDENT";
    }

    /** Full constructor (for reading from database) */
    public User(int id, String username, String password, String email,
                String fullName, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    // ---- Getters and Setters (Encapsulation) ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /** Check if user is admin */
    public boolean isAdmin() {
        return "TEACHER".equals(this.role);
    }

    public boolean isTeacher() {
        return "TEACHER".equals(this.role);
    }

    public boolean isStudent() {
        return "STUDENT".equals(this.role);
    }

    // ---- Method Overriding (toString) ----
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
