package com.example.society.admin.service;

import com.example.society.entity.Event;
import com.example.society.guest.entity.Visitor;
import com.example.society.model.Residence;
import com.example.society.repository.VisitorRepository;
import com.example.society.repository.ResidenceRepository;
import com.example.society.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private EventRepository eventRepository;

    // ================== Admin Dashboard ==================
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();

        long totalVisitors = visitorRepository.count();
        long approved = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.APPROVED));
        long rejected = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.REJECTED));
        long pending = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING));

        LocalDate today = LocalDate.now();
        long todayVisitors = visitorRepository.count((root, query, cb) ->
                cb.between(root.get("visitTime"), today.atStartOfDay(), today.plusDays(1).atStartOfDay()));

        data.put("totalVisitors", totalVisitors);
        data.put("approved", approved);
        data.put("rejected", rejected);
        data.put("pending", pending);
        data.put("todayVisitors", todayVisitors);

        // Fetch top 2 upcoming events for all buildings (Admin sees all)
        List<Event> upcomingEvents = eventRepository.findTop2ByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
        List<Map<String, Object>> events = upcomingEvents.stream()
                .map(event -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", event.getTitle());
                    map.put("description", event.getDescription());
                    map.put("date", event.getEventDate());
                    map.put("buildingNumber", event.getBuildingNumber());
                    map.put("flatNumber", event.getFlatNumber());
                    return map;
                })
                .collect(Collectors.toList());

        data.put("events", events);

        return data;
    }

    // ================== Resident Dashboard ==================
    public Map<String, Object> getResidentDashboard(String mobile) {
        Map<String, Object> data = new HashMap<>();

        Residence residence = residenceRepository.findByMobileNo(mobile).orElse(null);
        if (residence == null) {
            data.put("message", "No residence found for this resident.");
            data.put("success", false);
            return data;
        }

        String flatNumber = residence.getFlatNumber();
        String buildingNumber = residence.getBuildingNumber();

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

        LocalDate today = LocalDate.now();
        long todayVisitors = visitorRepository.count((root, query, cb) ->
                cb.between(root.get("visitTime"), today.atStartOfDay(), today.plusDays(1).atStartOfDay()));

        // Fetch top 2 upcoming events for resident's building
        List<Event> upcomingEvents = eventRepository.findTop2UpcomingEventsForBuilding(LocalDateTime.now(), buildingNumber);


        List<Map<String, Object>> events = upcomingEvents.stream()
                .map(event -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", event.getTitle());
                    map.put("description", event.getDescription());
                    map.put("date", event.getEventDate());
                    map.put("buildingNumber", event.getBuildingNumber());
                    map.put("flatNumber", event.getFlatNumber());
                    return map;
                })
                .collect(Collectors.toList());

        data.put("flatNumber", flatNumber);
        data.put("buildingNumber", buildingNumber);
        data.put("totalVisitors", totalVisitors);
        data.put("approved", approved);
        data.put("rejected", rejected);
        data.put("pending", pending);
        data.put("todayVisitors", todayVisitors);
        data.put("events", events);
        data.put("success", true);

        return data;
    }

    // ================== Guard Dashboard ==================
    public Map<String, Object> getGuardDashboard(String mobile) {
        Map<String, Object> data = new HashMap<>();

        long pendingVisitors = visitorRepository.count((root, query, cb) ->
                cb.equal(root.get("approveStatus"), Visitor.ApproveStatus.PENDING));
        data.put("pendingVisitors", pendingVisitors);

        // Fetch top 2 upcoming events for guard (all buildings)
        List<Event> upcomingEvents = eventRepository.findTop2ByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
        List<Map<String, Object>> events = upcomingEvents.stream()
                .map(event -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", event.getTitle());
                    map.put("description", event.getDescription());
                    map.put("date", event.getEventDate());
                    map.put("buildingNumber", event.getBuildingNumber());
                    map.put("flatNumber", event.getFlatNumber());
                    return map;
                })
                .collect(Collectors.toList());

        data.put("events", events);

        return data;
    }
}
