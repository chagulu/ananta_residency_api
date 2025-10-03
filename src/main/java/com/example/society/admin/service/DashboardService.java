package com.example.society.admin.service;

import com.example.society.guest.entity.Visitor;
import com.example.society.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private VisitorRepository visitorRepository;

    // Admin dashboard
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();

        // Total visitors
        long totalVisitors = visitorRepository.count();

        // Approved visitors
        long approved = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.APPROVED));

        // Rejected visitors
        long rejected = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.REJECTED));

        // Pending visitors
        long pending = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING));

        // Visitors today
        LocalDate today = LocalDate.now();
        long todayVisitors = visitorRepository.count((root, query, cb) ->
                cb.between(root.get("visitTime"), today.atStartOfDay(), today.plusDays(1).atStartOfDay()));

        data.put("totalVisitors", totalVisitors);
        data.put("approved", approved);
        data.put("rejected", rejected);
        data.put("pending", pending);
        data.put("todayVisitors", todayVisitors);

        // Example events (you can fetch from DB if you have an Event entity)
        data.put("events", List.of(
                Map.of("title", "Fire Drill", "date", "2025-10-05"),
                Map.of("title", "Annual Meeting", "date", "2025-10-10")
        ));

        return data;
    }


    // Inside DashboardService.java

        // Resident dashboard
        public Map<String, Object> getResidentDashboard(String mobile) {
        Map<String, Object> data = new HashMap<>();

        // Example: Fetch visitor stats for this resident
        long totalVisitors = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("residentMobile"), mobile));

        long approved = visitorRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("residentMobile"), mobile),
                        cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.APPROVED)
                ));

        long rejected = visitorRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("residentMobile"), mobile),
                        cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.REJECTED)
                ));

        long pending = visitorRepository.count((root, query, cb) ->
                cb.and(
                        cb.equal(root.get("residentMobile"), mobile),
                        cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING)
                ));

        data.put("totalVisitors", totalVisitors);
        data.put("approved", approved);
        data.put("rejected", rejected);
        data.put("pending", pending);

        return data;
        }

        // Guard dashboard
        public Map<String, Object> getGuardDashboard(String mobile) {
        Map<String, Object> data = new HashMap<>();

        // Example: Show pending visitor approvals for guards
        long pendingVisitors = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING));

        data.put("pendingVisitors", pendingVisitors);

        return data;
        }


}
