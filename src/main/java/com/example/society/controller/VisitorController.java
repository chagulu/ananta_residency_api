package com.example.society.controller;

import com.example.society.guest.entity.Visitor;
import com.example.society.jwt.JwtUtil;
import com.example.society.model.Residence;
import com.example.society.repository.VisitorRepository;
import com.example.society.service.OtpService;
import com.example.society.service.ResidenceService;
import com.example.society.specification.VisitorSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/visitor")
public class VisitorController {

    private static final Logger logger = LoggerFactory.getLogger(VisitorController.class);

    private final VisitorRepository visitorRepository;
    private final ResidenceService residenceService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Value("${app.base-url}")
    private String baseUrl;

    public VisitorController(
            VisitorRepository visitorRepository,
            ResidenceService residenceService,
            OtpService otpService,
            JwtUtil jwtUtil
    ) {
        this.visitorRepository = visitorRepository;
        this.residenceService = residenceService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * ✅ Fetch visitors for a particular residence (flat + building).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getVisitors(
            @RequestHeader(name = "Authorization") String authHeader,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String flatNumber,
            @RequestParam(required = false) String buildingNumber,
            @RequestParam(required = false) Visitor.ApproveStatus approveStatus,
            @RequestParam(defaultValue = "0") int page
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ Extract user info from token (if required for logging)
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            logger.info("Fetching visitors for user: {}", username);

            // ✅ Fetch residence by flat & building number
            if (flatNumber == null || buildingNumber == null) {
                response.put("success", false);
                response.put("message", "Flat number and Building number are required");
                return ResponseEntity.badRequest().body(response);
            }

            Residence residence = residenceService.getResidenceByFlatAndBuilding(buildingNumber, flatNumber);

            if (residence == null) {
                response.put("success", false);
                response.put("message", "Residence not found for provided flat and building number");
                response.put("data", null);
                return ResponseEntity.status(404).body(response);
            }

            // ✅ Prepare filters
            Map<String, String> filters = new HashMap<>();
            if (id != null) filters.put("id", id.toString());
            if (guestName != null) filters.put("guestName", guestName);
            if (mobile != null) filters.put("mobile", mobile);
            if (approveStatus != null) filters.put("approveStatus", approveStatus.name());

            // Always limit search to residence
            filters.put("flatNumber", residence.getFlatNumber());
            filters.put("buildingNumber", residence.getBuildingNumber());

            Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createdAt")));

            Page<Visitor> result = visitorRepository.findAll(
                    VisitorSpecification.getVisitorFilters(filters),
                    pageable
            );

            response.put("success", true);
            response.put("message", result.isEmpty() ? "No visitors found" : "Visitors fetched successfully");
            response.put("data", result.getContent());
            response.put("pagination", Map.of(
                    "currentPage", result.getNumber(),
                    "totalPages", result.getTotalPages(),
                    "totalItems", result.getTotalElements()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Error while fetching visitors", ex);
            response.put("success", false);
            response.put("message", "An error occurred while fetching visitors");
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * ✅ Guard endpoint - view all visitors
     */
    @GetMapping("/guard")
    public ResponseEntity<Map<String, Object>> getAllVisitorsForGuard(
            @RequestHeader(name = "Authorization") String authHeader,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String flatNumber,
            @RequestParam(required = false) String buildingNumber,
            @RequestParam(required = false) Visitor.ApproveStatus approveStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

            Map<String, String> filters = new HashMap<>();
            if (id != null) filters.put("id", id.toString());
            if (guestName != null) filters.put("guestName", guestName);
            if (mobile != null) filters.put("mobile", mobile);
            if (flatNumber != null) filters.put("flatNumber", flatNumber);
            if (buildingNumber != null) filters.put("buildingNumber", buildingNumber);
            if (approveStatus != null) filters.put("approveStatus", approveStatus.name());

            Page<Visitor> result = visitorRepository.findAll(
                    VisitorSpecification.getVisitorFilters(filters),
                    pageable
            );

            response.put("success", true);
            response.put("message", result.isEmpty() ? "No visitors found" : "Visitors fetched successfully");
            response.put("data", result.getContent());
            response.put("pagination", Map.of(
                    "currentPage", result.getNumber(),
                    "totalPages", result.getTotalPages(),
                    "totalItems", result.getTotalElements()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error while fetching visitors for guard", ex);
            response.put("success", false);
            response.put("message", "An error occurred while fetching visitors");
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * ✅ Approve visitor by token link
     */
    @GetMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveVisitor(@RequestParam("token") String token) {
        logger.info("Received approval request for token: {}", token);

        Map<String, Object> response = new HashMap<>();

        Optional<Visitor> visitorOpt = visitorRepository.findByToken(token);

        if (visitorOpt.isEmpty()) {
            logger.warn("Invalid or expired token used: {}", token);
            response.put("success", false);
            response.put("message", "Invalid or expired approval token.");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        Visitor visitor = visitorOpt.get();
        visitor.setApproveStatus(Visitor.ApproveStatus.APPROVED);
        visitorRepository.save(visitor);

        logger.info("Visitor with token {} approved successfully", token);

        Map<String, Object> visitorData = new HashMap<>();
        visitorData.put("guestName", visitor.getGuestName());
        visitorData.put("mobile", visitor.getMobile());
        visitorData.put("flatNumber", visitor.getFlatNumber());
        visitorData.put("buildingNumber", visitor.getBuildingNumber());
        visitorData.put("visitTime", visitor.getVisitTime());

        response.put("success", true);
        response.put("message", "Visitor approved successfully.");
        response.put("data", visitorData);

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Approve or Reject visitor manually by resident (flat + building check)
     */
    @PostMapping("/{id}/action")
    public ResponseEntity<Map<String, Object>> handleVisitorAction(
            @PathVariable Long id,
            @RequestParam("action") String action,
            @RequestParam("flatNumber") String flatNumber,
            @RequestParam("buildingNumber") String buildingNumber
    ) {
        logger.info("Received {} request for visitorId: {}", action, id);
        Map<String, Object> response = new HashMap<>();

        try {
            Residence residence = residenceService.getResidenceByFlatAndBuilding(buildingNumber, flatNumber);
            if (residence == null) {
                response.put("success", false);
                response.put("message", "Residence not found for given flat/building");
                return ResponseEntity.status(404).body(response);
            }

            Optional<Visitor> visitorOpt = visitorRepository.findById(id);
            if (visitorOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Visitor not found");
                return ResponseEntity.badRequest().body(response);
            }

            Visitor visitor = visitorOpt.get();

            // Ensure visitor belongs to same residence
            if (!visitor.getFlatNumber().equals(residence.getFlatNumber()) ||
                !visitor.getBuildingNumber().equals(residence.getBuildingNumber())) {
                response.put("success", false);
                response.put("message", "You are not authorized to modify this visitor");
                return ResponseEntity.status(403).body(response);
            }

            if ("approve".equalsIgnoreCase(action)) {
                visitor.setApproveStatus(Visitor.ApproveStatus.APPROVED);
            } else if ("reject".equalsIgnoreCase(action)) {
                visitor.setApproveStatus(Visitor.ApproveStatus.REJECTED);
            } else {
                response.put("success", false);
                response.put("message", "Invalid action. Must be 'approve' or 'reject'.");
                return ResponseEntity.badRequest().body(response);
            }

            visitorRepository.save(visitor);

            Map<String, Object> visitorData = new HashMap<>();
            visitorData.put("guestName", visitor.getGuestName());
            visitorData.put("mobile", visitor.getMobile());
            visitorData.put("flatNumber", visitor.getFlatNumber());
            visitorData.put("buildingNumber", visitor.getBuildingNumber());
            visitorData.put("visitTime", visitor.getVisitTime());
            visitorData.put("status", visitor.getApproveStatus());

            response.put("success", true);
            response.put("message", "Visitor " + action + "d successfully.");
            response.put("data", visitorData);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Error while processing visitor {} action {}", id, action, ex);
            response.put("success", false);
            response.put("message", "An error occurred while processing visitor action");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
