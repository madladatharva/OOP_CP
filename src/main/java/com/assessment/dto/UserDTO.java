package com.assessment.dto;

import com.assessment.model.User;

/**
 * Data Transfer Object for User.
 * Ensures passwords and sensitive database fields are not passed to the
 * frontend.
 */
public class UserDTO {
    private int id;
    private String username;
    private String email;
    private String fullName;
    private String role;

    public UserDTO(int id, String username, String email, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public static UserDTO fromEntity(User user) {
        if (user == null)
            return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole());
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
}
