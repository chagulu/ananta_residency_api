package com.example.society.admin.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    // Example: Admin dashboard
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalVisitors", 120);
        data.put("approved", 90);
        data.put("rejected", 20);
        data.put("pending", 10);
        data.put("events", List.of(
                Map.of("title", "Fire Drill", "date", "2025-10-05"),
                Map.of("title", "Annual Meeting", "date", "2025-10-10")
        ));
        return data;
    }

    // Example: Resident dashboard
    public Map<String, Object> getResidentDashboard(String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("myVisitorsToday", 5);
        data.put("pendingApprovals", 2);
        data.put("approvedVisitors", 15);
        data.put("events", List.of(
                Map.of("title", "Community Meeting", "date", "2025-10-06")
        ));
        return data;
    }

    // Example: Guard dashboard
    public Map<String, Object> getGuardDashboard(String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("todayVisitors", 50);
        data.put("pendingApprovals", 5);
        data.put("checkedInVisitors", 45);
        data.put("events", List.of());
        return data;
    }
}
