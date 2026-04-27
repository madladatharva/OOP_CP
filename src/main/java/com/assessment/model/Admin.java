package com.assessment.model;

import java.sql.Timestamp;

/**
 * Admin Model Class - Demonstrates INHERITANCE
 * 
 * OOP Concepts:
 * - Inheritance: Admin extends User (IS-A relationship)
 * - Method Overriding: Overrides toString() and adds admin-specific behavior
 * - Constructor Overloading: Multiple constructors chaining to parent
 */
public class Admin extends User {

    // ---- Additional fields specific to Admin ----
    private String department;

    // ---- Constructor Overloading with Inheritance ----

    /** Default constructor using super() */
    public Admin() {
        super();
        setRole("ADMIN");
        this.department = "Administration";
    }

    /** Constructor for login */
    public Admin(String username, String password) {
        super(username, password);
        setRole("ADMIN");
        this.department = "Administration";
    }

    /** Full constructor */
    public Admin(int id, String username, String password, String email,
                 String fullName, String role, Timestamp createdAt) {
        super(id, username, password, email, fullName, role, createdAt);
        this.department = "Administration";
    }

    // ---- Getters and Setters ----

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // ---- Method Overriding ----
    @Override
    public boolean isAdmin() {
        return true; // Admin is always admin
    }

    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", username='" + getUsername()
                + "', department='" + department + "'}";
    }
}
