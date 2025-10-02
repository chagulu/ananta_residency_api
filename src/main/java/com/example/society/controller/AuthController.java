package com.example.society.controller;

import com.example.society.dto.JwtResponse;
import com.example.society.dto.OtpRequest;
import com.example.society.dto.UserDto;
import com.example.society.model.Guard;
import com.example.society.repository.UserRepository;
import com.example.society.service.JwtService;
import com.example.society.service.OtpService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;


import java.util.*;

@RestController
@RequestMapping("/guard/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;


    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDto userDto) {
        try {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Username already exists"
                ));
            }

            if (userRepository.existsByMobileNo(userDto.getMobileNo())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Mobile number already registered"
                ));
            }

            Guard user = new Guard(
                    userDto.getUsername(),
                    passwordEncoder.encode(userDto.getPassword()),
                    userDto.getMobileNo(),
                    userDto.getEmail(),
                    userDto.getResidenceName(),
                    userDto.getFlatNumber(),
                    userDto.getBuildingNumber()
            );

            userRepository.save(user);

            String token = jwtService.generateToken(user.getMobileNo());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Registration failed",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");

        Optional<Guard> userOpt = userRepository.findByMobileNo(mobileNo);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Mobile number not registered"
            ));
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        otpStore.put(mobileNo, otp);

        boolean sent = otpService.sendOtp(mobileNo, otp);

        if (sent) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "OTP sent"
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Failed to send OTP"
            ));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest) {
        String sentOtp = otpStore.get(otpRequest.getMobileNo());

        if (sentOtp != null && sentOtp.equals(otpRequest.getOtp())) {
            String token = jwtService.generateToken(otpRequest.getMobileNo());
            otpStore.remove(otpRequest.getMobileNo());

            // ✅ Return JwtResponse with role
            return ResponseEntity.ok(new JwtResponse(token, true, "ROLE_GUARD"));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid OTP"
        ));
    }


        @GetMapping("/check-token")
        public ResponseEntity<Map<String, Object>> checkToken(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            Map<String, Object> body = new HashMap<>();

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                body.put("active", false);
                body.put("expired", false);
                body.put("message", "Missing or malformed Authorization header");
                return ResponseEntity.status(401).body(body);
            }

            String token = authHeader.substring(7);

            try {
                if (jwtService.isTokenExpired(token)) {
                    Date expires = jwtService.getExpirationDate(token);
                    body.put("active", false);
                    body.put("expired", true);
                    body.put("message", "Token has expired");
                    body.put("expiresAt", expires != null ? expires.toInstant().toString() : null);
                    return ResponseEntity.status(401).body(body);
                }

                String username = jwtService.extractUsername(token);
                List<String> roles = jwtService.extractRoles(token);
                Date issued = jwtService.getIssuedAt(token);
                Date expires = jwtService.getExpirationDate(token);

                body.put("active", true);
                body.put("expired", false);
                body.put("username", username);
                body.put("roles", roles);
                body.put("issuedAt", issued != null ? issued.toInstant().toString() : null);
                body.put("expiresAt", expires != null ? expires.toInstant().toString() : null);

                return ResponseEntity.ok(body);

            } catch (Exception e) {
                body.put("active", false);
                body.put("expired", false);
                body.put("message", "Invalid or malformed token");
                return ResponseEntity.status(401).body(body);
            }
        }

    }

    

