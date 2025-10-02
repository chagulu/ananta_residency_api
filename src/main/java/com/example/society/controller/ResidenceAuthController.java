package com.example.society.controller;

import com.example.society.dto.JwtResponse;
import com.example.society.dto.OtpRequest;
import com.example.society.model.Residence;
import com.example.society.repository.ResidenceRepository;
import com.example.society.service.JwtService;
import com.example.society.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/residence/auth")
public class ResidenceAuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private OtpService otpService;

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");

        Optional<Residence> resOpt = residenceRepository.findByMobileNo(mobileNo);
        if (resOpt.isEmpty()) {
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
                return ResponseEntity.ok(new JwtResponse(token, true, "ROLE_RESIDENT"));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid OTP"
            ));
        }

}
