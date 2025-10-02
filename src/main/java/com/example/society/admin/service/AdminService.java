package com.example.society.admin.service;

import com.example.society.admin.dto.AdminDto;
import com.example.society.admin.dto.LoginRequest;
import com.example.society.admin.dto.SubAdminRegisterRequest;
import com.example.society.admin.entity.Admin;
import com.example.society.admin.entity.LoginLog;
import com.example.society.admin.repository.AdminRepository;
import com.example.society.admin.repository.LoginLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.society.service.JwtService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AdminService(AdminRepository adminRepository,
                        LoginLogRepository loginLogRepository,
                        @Qualifier("adminPasswordEncoder") PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.loginLogRepository = loginLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authenticate(AdminDto adminDto) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(adminDto.getUsername());
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (passwordEncoder.matches(adminDto.getPassword(), admin.getPassword())) {
                logLogin(admin.getUsername());
                return "Login successful";
            }
        }
        return "Invalid credentials";
    }

    public String createSubAdmin(AdminDto dto) {
        if (adminRepository.existsByUsername(dto.getUsername())) {
            return "Username already exists";
        }

        if (adminRepository.existsByEmail(dto.getEmail())) {
            return "Email already exists";
        }

        Admin admin = Admin.builder()
                .username(dto.getUsername())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Admin.Role.SUB_ADMIN)
                .active(true)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        adminRepository.save(admin);
        return "Sub-admin created successfully";
    }

    private void logLogin(String username) {
        LoginLog log = LoginLog.builder()
                .username(username)
                .loginTime(LocalDateTime.now())
                .ipAddress("127.0.0.1") // TODO: Replace with actual IP fetching logic
                .build();
        loginLogRepository.save(log);
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(loginRequest.getUsername());

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();

            if (!admin.getActive()) {
                return ResponseEntity.status(403).body(Map.of(
                        "status", "error",
                        "message", "Account is disabled"
                ));
            }

            if (passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                logLogin(admin.getUsername());

                // Generate JWT token passing username and roles
                String token = generateJwtToken(admin);

                // Return token and username
                return ResponseEntity.ok().body(Map.of(
                        "token", token,
                        "username", admin.getUsername()
                ));
            }
        }

        return ResponseEntity.status(401).body(Map.of(
                "status", "error",
                "message", "Invalid credentials"
        ));
    }

    // Generate JWT token with username and roles
    private String generateJwtToken(Admin admin) {
        // Pass role name as a list to JwtService
        return jwtService.generateToken(admin.getUsername(), List.of(admin.getRole().name()));
    }

    public String registerSubAdmin(SubAdminRegisterRequest request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        if (adminRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Admin.Role.SUB_ADMIN)
                .active(true)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        adminRepository.save(admin);
        return "Sub-admin registered successfully.";
    }
}
