package com.example.society.guest.controller;

import com.example.society.guest.dto.GuestVisitRequest;
import com.example.society.guest.entity.Visitor;
import com.example.society.guest.repository.GuestEntryRepository;
import com.example.society.jwt.JwtUtil;
import com.example.society.service.ResidenceService;
import com.example.society.service.OtpService;
import com.example.society.dto.guest.QrTokenResponse;
import com.example.society.dto.guest.QrEntryInitResponse;
import com.example.society.dto.guest.QrEntryRequest;
import com.example.society.dto.guest.VerifyGuestRequest;
import com.example.society.dto.guest.PendingGuestPayload;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visitor")
@CrossOrigin(origins = GuestVisitController.ALLOWED_ORIGIN)
public class GuestVisitController {

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private OtpService otpService;

    @Autowired
    private ResidenceService residenceService;

    private static final Logger logger = LoggerFactory.getLogger(GuestVisitController.class);
    public static final String ALLOWED_ORIGIN = "http://localhost:8080"; // Change in one place only

    private final JwtUtil jwtUtil;
    private final GuestEntryRepository guestEntryRepository;

    // In-memory pending store; replace with Redis/Caffeine in production
    private final Map<String, PendingGuestPayload> pendingStore = new ConcurrentHashMap<>();

    public GuestVisitController(JwtUtil jwtUtil, GuestEntryRepository guestEntryRepository) {
        this.jwtUtil = jwtUtil;
        this.guestEntryRepository = guestEntryRepository;
    }

    // =========================
    // Manual Guard Entry (keep)
    // =========================
    @PostMapping("/entry")
    public ResponseEntity<Map<String, Object>> recordGuestEntry(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody GuestVisitRequest request,
            BindingResult bindingResult) {

        logger.info("Received request to record guest entry");

        try {
            // Validate Authorization header presence and format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header is missing or malformed");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Missing or invalid Authorization header");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7);

            // Validate JWT token
            if (!jwtUtil.validateToken(token)) {
                logger.warn("JWT token is invalid or expired");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Handle validation errors
            if (bindingResult.hasErrors()) {
                String errorMsg = bindingResult.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining("; "));
                logger.warn("Validation errors: {}", errorMsg);

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", errorMsg);
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // Extract user identifier (e.g., mobile number) from JWT token
            String userMobile = jwtUtil.extractUsername(token);
            logger.debug("Authenticated user mobile from token: {}", userMobile);

            // Generate unique approval token BEFORE saving visitor
            String approvalToken = UUID.randomUUID().toString();

            // Build visitor entity
            Visitor visitor = new Visitor();
            visitor.setGuestName(request.getGuestName());
            visitor.setMobile(request.getMobile());
            visitor.setFlatNumber(request.getFlatNumber());
            visitor.setBuildingNumber(request.getBuildingNumber());
            visitor.setVisitPurpose(request.getVisitPurpose());
            visitor.setVehicleDetails(request.getVehicleDetails());
            visitor.setVisitTime(LocalDateTime.now());
            visitor.setCreatedBy(userMobile);
            visitor.setToken(approvalToken);  // approval token for first-approver link
            // Ensure default approve_status=PENDING via entity default or set here if needed
            try {
                visitor.setApproveStatus(Visitor.ApproveStatus.PENDING);

            } catch (Exception ignore) {
                // if enum default is already handled in entity/DB
            }

            // Save the entry with token
            guestEntryRepository.save(visitor);
            logger.info("Guest entry saved successfully for guest: {}", visitor.getGuestName());

            // Build approval link with token
            String approvalLink = baseUrl + "/api/visitor/approve?token=" + approvalToken;

            // Compose message with short approval link
            String message = "Guest " + visitor.getGuestName() + " wants to visit. Approve or reject: " + approvalLink;

            // Fetch resident mobile by flat/building
            String residentMobile = residenceService.getResidentMobile(visitor.getFlatNumber(), visitor.getBuildingNumber());

            // Send SMS to resident
            boolean smsSent = otpService.sendOtp(residentMobile, message); // ideally use a messaging method, not OTP
            if (!smsSent) {
                logger.warn("Failed to send approval SMS to resident mobile: {}", residentMobile);
            }

            // Prepare response data
            Map<String, Object> visitorData = new HashMap<>();
            visitorData.put("guestName", visitor.getGuestName());
            visitorData.put("mobile", visitor.getMobile());
            visitorData.put("flatNumber", visitor.getFlatNumber());
            visitorData.put("buildingNumber", visitor.getBuildingNumber());
            visitorData.put("visitPurpose", visitor.getVisitPurpose());
            visitorData.put("vehicleDetails", visitor.getVehicleDetails());
            visitorData.put("visitTime", visitor.getVisitTime() != null ? visitor.getVisitTime().toString() : null);
            visitorData.put("approvalLink", approvalLink);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Guest entry recorded successfully and approval link sent");
            response.put("data", visitorData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error occurred while recording guest entry", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to record guest entry due to server error");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test successful");
    }

    @PutMapping("/entry/{id}")
    public ResponseEntity<Map<String, Object>> editGuestEntry(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long id,
            @RequestBody GuestVisitRequest request) {

        logger.info("Received request to edit guest entry with ID: {}", id);

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header is missing or malformed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Missing or invalid Authorization header",
                        "data", null
                ));
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                logger.warn("JWT token is invalid or expired: {}", token);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "success", false,
                        "message", "Invalid or expired token",
                        "data", null
                ));
            }

            String userMobile = jwtUtil.extractUsername(token);
            logger.debug("Authenticated user mobile from token: {}", userMobile);

            Visitor visitor = guestEntryRepository.findById(id).orElse(null);
            if (visitor == null) {
                logger.warn("Guest entry not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Guest entry not found",
                        "data", null
                ));
            }

            visitor.setGuestName(request.getGuestName());
            visitor.setMobile(request.getMobile());
            visitor.setFlatNumber(request.getFlatNumber());
            visitor.setBuildingNumber(request.getBuildingNumber());
            visitor.setVisitPurpose(request.getVisitPurpose());
            visitor.setVehicleDetails(request.getVehicleDetails());
            //visitor.setUpdatedBy(userMobile);
            visitor.setVisitTime(LocalDateTime.now());

            guestEntryRepository.save(visitor);
            logger.info("Guest entry updated for: {}", visitor.getGuestName());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Guest entry updated successfully",
                    "data", visitor
            ));

        } catch (Exception e) {
            logger.error("Unexpected error while updating guest entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to update guest entry",
                    "data", null
            ));
        }
    }

    // 1) Guard app requests short-lived rotating QR token (rotate every 30–60s)
    @GetMapping("/qr-token")
    public ResponseEntity<QrTokenResponse> getQrToken(
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            QrTokenResponse r = new QrTokenResponse();
            r.setSuccess(false);
            r.setMessage("Unauthorized");
            r.setQrToken(null);
            r.setExpiresInSeconds(0);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);

        }
        String bearer = authHeader.substring(7);
        if (!jwtUtil.validateToken(bearer)) {
            QrTokenResponse r = new QrTokenResponse();
            r.setSuccess(false);
            r.setMessage("Invalid or expired guard token");
            r.setQrToken(null);
            r.setExpiresInSeconds(0);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);

        }

        long ttlSeconds = 45; // recommended rotation window
        String qrToken = jwtUtil.generateTokenWithCustomTTL("QR_ENTRY", Duration.ofSeconds(ttlSeconds)); // add helper in JwtUtil
        QrTokenResponse resp = new QrTokenResponse();
        resp.setSuccess(true);
        resp.setMessage("QR token generated");
        resp.setQrToken(qrToken);
        resp.setExpiresInSeconds(ttlSeconds);
        return ResponseEntity.ok(resp);

    }

    // 2) Guest scans QR and submits form; system validates QR token and sends OTP
    @PostMapping("/qr-entry")
    public ResponseEntity<QrEntryInitResponse> qrEntryInit(@RequestBody QrEntryRequest req) {
        if (!StringUtils.hasText(req.getQrToken()) || !jwtUtil.validateToken(req.getQrToken())) {
            QrEntryInitResponse r = new QrEntryInitResponse();
            r.setSuccess(false);
            r.setMessage("Invalid or expired QR token");
            r.setCorrelationId(null);
            r.setOtpChannel(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);
        }
        if (!StringUtils.hasText(req.getMobile()) || !StringUtils.hasText(req.getFlatNumber()) || !StringUtils.hasText(req.getBuildingNumber())) {
            QrEntryInitResponse r = new QrEntryInitResponse();
            r.setSuccess(false);
            r.setMessage("Missing mobile/flatNumber/buildingNumber");
            r.setCorrelationId(null);
            r.setOtpChannel(null);
            return ResponseEntity.badRequest().body(r);
        }


        boolean sent = otpService.sendOtp(req.getMobile(), null);
        if (!sent) {
            QrEntryInitResponse r = new QrEntryInitResponse();
            r.setSuccess(false);
            r.setMessage("Failed to send OTP");
            r.setCorrelationId(null);
            r.setOtpChannel(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(r);
        }

        String correlationId = UUID.randomUUID().toString();
        PendingGuestPayload pending = new PendingGuestPayload();
        pending.setCorrelationId(correlationId);
        pending.setMobile(req.getMobile());
        pending.setGuestName(req.getGuestName());
        pending.setFlatNumber(req.getFlatNumber());
        pending.setBuildingNumber(req.getBuildingNumber());
        pending.setVisitPurpose(req.getVisitPurpose());
        pending.setVehicleDetails(req.getVehicleDetails());
        pending.setCreatedBy("QR");
        pending.setIdempotencyKey(req.getIdempotencyKey());
        pending.setCreatedAt(LocalDateTime.now());
        pending.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        pendingStore.put(correlationId, pending);

        QrEntryInitResponse resp = new QrEntryInitResponse();
        resp.setSuccess(true);
        resp.setMessage("OTP sent. Verify to proceed.");
        resp.setCorrelationId(correlationId);
        resp.setOtpChannel("SMS");
        return ResponseEntity.ok(resp); 
    }


    // 3) Guest verifies OTP; persist PENDING visitor and notify resident (approval token)
    @PostMapping("/verify-guest")
    public ResponseEntity<Map<String, Object>> verifyGuestOtp(@RequestBody VerifyGuestRequest req) {
    // Validate request fields
    if (!StringUtils.hasText(req.getCorrelationId()) || !StringUtils.hasText(req.getMobile()) || !StringUtils.hasText(req.getOtpCode())) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Missing correlationId/mobile/otpCode",
                "data", null
        ));
    }

    // Lookup pending session
    PendingGuestPayload pending = pendingStore.get(req.getCorrelationId());
    if (pending == null || pending.getExpiresAt().isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of(
                "success", false,
                "message", "Session expired or not found",
                "data", null
        ));
    }

    // Ensure same mobile
    if (!pending.getMobile().equals(req.getMobile())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Mobile mismatch",
                "data", null
        ));
    }

    // Verify OTP
    if (!otpService.verifyOtp(req.getMobile(), req.getOtpCode())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Invalid OTP",
                "data", null
        ));
    }

    // Create approval token
    String approvalToken = UUID.randomUUID().toString();

    // Persist visitor as PENDING
    Visitor visitor = new Visitor();
    visitor.setGuestName(pending.getGuestName());
    visitor.setMobile(pending.getMobile());
    visitor.setFlatNumber(pending.getFlatNumber());
    visitor.setBuildingNumber(pending.getBuildingNumber());
    visitor.setVisitPurpose(pending.getVisitPurpose());
    visitor.setVehicleDetails(pending.getVehicleDetails());
    visitor.setVisitTime(LocalDateTime.now());
    visitor.setToken(approvalToken);
    // Use enum, not String
    visitor.setApproveStatus(Visitor.ApproveStatus.PENDING);
    visitor.setCreatedBy(pending.getCreatedBy());

    guestEntryRepository.save(visitor);

    // Notify resident with approval link
    String approvalLink = baseUrl + "/api/visitor/approve?token=" + approvalToken;
    String residentMobile = residenceService.getResidentMobile(visitor.getFlatNumber(), visitor.getBuildingNumber());
    String message = "Guest " + (StringUtils.hasText(visitor.getGuestName()) ? visitor.getGuestName() : visitor.getMobile())
            + " requests entry. Approve/Reject: " + approvalLink;
    boolean smsSent = otpService.sendOtp(residentMobile, message);
    if (!smsSent) {
        logger.warn("Failed to send approval link SMS to resident {}", residentMobile);
    }

    // Cleanup pending
    pendingStore.remove(req.getCorrelationId());

    Map<String, Object> data = new HashMap<>();
    data.put("visitorId", visitor.getId());
    data.put("approvalLink", approvalLink);
    data.put("status", visitor.getApproveStatus());

    return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Guest verified. Visit created as PENDING and approval sent.",
            "data", data
    ));
}

}
