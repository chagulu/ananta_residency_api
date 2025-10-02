package com.example.society.model;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_RESIDENT,
    ROLE_GUARD;

    public static UserRole fromString(String role) {
        if (role == null) throw new IllegalArgumentException("Role cannot be null");
        switch (role.toUpperCase()) {
            case "ROLE_ADMIN":
            case "ADMIN":
                return ROLE_ADMIN;
            case "ROLE_RESIDENT":
            case "RESIDENT":
                return ROLE_RESIDENT;
            case "ROLE_GUARD":
            case "GUARD":
                return ROLE_GUARD;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
