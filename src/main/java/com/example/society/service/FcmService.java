package com.example.society.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {

    // Existing method
    public String sendNotification(String fcmToken, String title, String body) throws Exception {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    // 🔹 New overloaded method with data payload
    public String sendNotification(String fcmToken, String title, String body, Map<String, String> data) throws Exception {
        Message.Builder builder = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (data != null && !data.isEmpty()) {
            builder.putAllData(data); // add deep link / route info
        }

        Message message = builder.build();
        return FirebaseMessaging.getInstance().send(message);
    }
}
