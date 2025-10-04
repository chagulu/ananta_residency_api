package com.example.society.controller;

import com.example.society.admin.service.DashboardService;
import com.example.society.jwt.JwtUtil;
import com.example.society.repository.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ResidenceRepository residenceRepository;

    // ------------------ Admin Dashboard ------------------
    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = dashboardService.getAdminDashboard();
            response.put("success", true);
            response.put("message", "Admin dashboard loaded successfully");
            response.put("data", data);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load admin dashboard: " + e.getMessage());
            response.put("data", null);
        }
        return ResponseEntity.ok(response);
    }

    // ------------------ Resident Dashboard ------------------
    @GetMapping("/resident/dashboard")
    public ResponseEntity<Map<String, Object>> getResidentDashboard(
            @RequestHeader(name = "Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            String mobile = jwtUtil.extractUsername(token);

            Map<String, Object> data = dashboardService.getResidentDashboard(mobile);
            response.put("success", true);
            response.put("message", "Resident dashboard loaded successfully");
            response.put("data", data);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load resident dashboard: " + e.getMessage());
            response.put("data", null);
        }
        return ResponseEntity.ok(response);
    }

    // ------------------ Guard Dashboard ------------------
    @GetMapping("/guard/dashboard")
    public ResponseEntity<Map<String, Object>> getGuardDashboard(
            @RequestHeader(name = "Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            String mobile = jwtUtil.extractUsername(token);

            Map<String, Object> data = dashboardService.getGuardDashboard(mobile);
            response.put("success", true);
            response.put("message", "Guard dashboard loaded successfully");
            response.put("data", data);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to load guard dashboard: " + e.getMessage());
            response.put("data", null);
        }
        return ResponseEntity.ok(response);
    }
}
