package com.example.society.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.society.repository.NotificationRepository;
import com.example.society.repository.NotificationRecipientRepository;
import com.example.society.repository.ResidenceRepository;
import com.example.society.model.NotificationRecipient;
import com.example.society.entity.NotificationStatus;
import com.example.society.entity.NotificationType;
import com.example.society.guest.entity.NotificationEntity;
import com.example.society.model.Residence;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final FcmService fcmService;
    private final ResidenceRepository residenceRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationRecipientRepository recipientRepository,
                               FcmService fcmService,
                               ResidenceRepository residenceRepository) {
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.fcmService = fcmService;
        this.residenceRepository = residenceRepository;
    }

    @Transactional
    public void sendNotificationToFlat(Long flatId, String title, String message, Long adminId, String fcmToken) {
        NotificationEntity notification = new NotificationEntity();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(NotificationType.INDIVIDUAL);
        notification.setFlatId(flatId);
        notification.setCreatedBy(adminId);

        notification = notificationRepository.save(notification);

        try {
            String response = fcmService.sendNotification(fcmToken, title, message);
            notification.setStatus(NotificationStatus.SENT);

            NotificationRecipient recipient = new NotificationRecipient();
            recipient.setNotificationId(notification.getId());
            recipient.setFlatId(flatId);
            recipient.setDeliveredAt(java.time.LocalDateTime.now());
            recipientRepository.save(recipient);

            System.out.println("FCM Response: " + response);

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            e.printStackTrace();
        }

        notificationRepository.save(notification);
    }

    /**
     * Save or update the FCM token for a residence using residence ID
     */
    @Transactional
    public void saveFcmToken(Long residenceId, String fcmToken) {
        Residence residence = residenceRepository.findById(residenceId)
                .orElseThrow(() -> new RuntimeException("Residence not found with id: " + residenceId));
        residence.setFcmToken(fcmToken);
        residenceRepository.save(residence);
        System.out.println("✅ FCM token saved for residenceId=" + residenceId);
    }

    /**
     * Save or update the FCM token for a residence using mobile number
     */
    @Transactional
    public void saveFcmTokenByMobile(String mobileNo, String fcmToken) {
        Residence residence = residenceRepository.findByMobileNo(mobileNo)
                .orElseThrow(() -> new RuntimeException("Residence not found with mobileNo: " + mobileNo));
        residence.setFcmToken(fcmToken);
        residenceRepository.save(residence);
        System.out.println("✅ FCM token saved for mobileNo=" + mobileNo);
    }
}
