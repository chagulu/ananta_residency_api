package com.example.society.dto;

public class JwtResponse {
    private String token;
    private boolean success;
    private String role;  // ✅ new field

    public JwtResponse(String token, boolean success, String role) {
        this.token = token;
        this.success = success;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
