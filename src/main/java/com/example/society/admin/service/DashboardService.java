package com.example.society.admin.service;

import com.example.society.guest.entity.Visitor;
import com.example.society.model.Residence;
import com.example.society.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.society.repository.ResidenceRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private ResidenceRepository residenceRepository;

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

        // Example events (static for now, can fetch from DB if available)
        data.put("events", List.of(
                Map.of("title", "Fire Drill", "date", "2025-10-05"),
                Map.of("title", "Annual Meeting", "date", "2025-10-10")
        ));

        return data;
    }

    // Resident dashboard
    public Map<String, Object> getResidentDashboard(String mobile) {
    Map<String, Object> data = new HashMap<>();

    // Step 1: Get residence info for this resident (flat & building)
    Residence residence = residenceRepository.findByMobileNo(mobile)
            .orElse(null);

    if (residence == null) {
        data.put("message", "No residence found for this resident.");
        data.put("success", false);
        return data;
    }

    String flatNumber = residence.getFlatNumber();
    String buildingNumber = residence.getBuildingNumber();

    // Step 2: Fetch visitor stats for this residence
    long totalVisitors = visitorRepository.count((root, query, cb) ->
            cb.and(
                    cb.equal(root.get("flatNumber"), flatNumber),
                    cb.equal(root.get("buildingNumber"), buildingNumber)
            ));

    long approved = visitorRepository.count((root, query, cb) ->
            cb.and(
                    cb.equal(root.get("flatNumber"), flatNumber),
                    cb.equal(root.get("buildingNumber"), buildingNumber),
                    cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.APPROVED)
            ));

    long rejected = visitorRepository.count((root, query, cb) ->
            cb.and(
                    cb.equal(root.get("flatNumber"), flatNumber),
                    cb.equal(root.get("buildingNumber"), buildingNumber),
                    cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.REJECTED)
            ));

    long pending = visitorRepository.count((root, query, cb) ->
            cb.and(
                    cb.equal(root.get("flatNumber"), flatNumber),
                    cb.equal(root.get("buildingNumber"), buildingNumber),
                    cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING)
            ));

    // Step 3: Populate dashboard data
    data.put("flatNumber", flatNumber);
    data.put("buildingNumber", buildingNumber);
    data.put("totalVisitors", totalVisitors);
    data.put("approved", approved);
    data.put("rejected", rejected);
    data.put("pending", pending);
    data.put("success", true);

    return data;
}


    // Guard dashboard
    public Map<String, Object> getGuardDashboard(String mobile) {
        Map<String, Object> data = new HashMap<>();

        // Show pending visitor approvals for guards
        long pendingVisitors = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING));

        data.put("pendingVisitors", pendingVisitors);

        return data;
    }
}
