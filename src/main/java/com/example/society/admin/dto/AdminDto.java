package com.example.society.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin data transfer object for authentication and profile updates.
 * Lombok @Data generates getters, setters, equals/hashCode, and toString.
 * No-args and all-args constructors are provided for flexibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private String username;
    private String password;
    private String email;
    private String name;
}
