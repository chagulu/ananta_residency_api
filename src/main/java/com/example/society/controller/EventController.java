package com.example.society.controller;

import com.example.society.entity.Event;
import com.example.society.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.society.jwt.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new event (admin only) and send notifications to residents.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestBody Event event) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Extract admin username from JWT
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Missing Authorization header");
                return ResponseEntity.status(401).body(response);
            }

            String token = authHeader.substring(7);
            String adminUsername = jwtUtil.extractUsername(token); // get username from token
            event.setCreatedBy(adminUsername); // ✅ set createdBy before saving

            Event savedEvent = eventService.createEvent(event);

            response.put("success", true);
            response.put("message", "Event created successfully and notifications sent.");
            response.put("data", savedEvent);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to create event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    /**
     * Get all events (for residents to view upcoming events)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", eventService.getAllEvents());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to fetch events: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get event details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Event event = eventService.getEventById(id);
            if (event != null) {
                response.put("success", true);
                response.put("data", event);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Event not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error retrieving event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
