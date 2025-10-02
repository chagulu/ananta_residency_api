package com.example.society.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.society.service.NotificationService;
import com.example.society.dto.NotificationRequest;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendNotificationToFlat(
                request.getFlatId(),
                request.getTitle(),
                request.getMessage(),
                request.getAdminId(),
                request.getFcmToken()
        );
        return "Notification processed";
    }

    
    // ✅ New endpoint to save/update FCM token using mobile number
        @PostMapping("/register-token")
        public ResponseEntity<String> registerFcmToken(@RequestBody NotificationRequest request) {
            notificationService.saveFcmTokenByMobile(request.getMobileNo(), request.getFcmToken());
            return ResponseEntity.ok("FCM token saved successfully");
        }



}
