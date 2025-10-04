package com.example.society.scheduler;

import com.example.society.entity.Event;
import com.example.society.model.Residence;
import com.example.society.repository.EventRepository;
import com.example.society.repository.ResidenceRepository;
import com.example.society.service.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
public class EventNotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventNotificationScheduler.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private FcmService fcmService;

    /**
     * Run once a day at 6 AM to send event notifications
     */
    @Scheduled(cron = "0 0 6 * * ?") // 6:00 AM every day
    // @Scheduled(cron = "0 * * * * *") // Every minute
    public void sendEventNotifications() {
        logger.info("Running EventNotificationScheduler...");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Fetch only events happening today or tomorrow
        List<Event> events = eventRepository.findByEventDateBetween(
                today.atStartOfDay(),
                tomorrow.atTime(LocalTime.MAX)
        );

        for (Event event : events) {
            LocalDate eventDate = event.getEventDate().toLocalDate();

            boolean isTomorrow = eventDate.equals(tomorrow);
            boolean isToday = eventDate.equals(today) && LocalTime.now().isBefore(LocalTime.of(9, 0));

            if (isTomorrow || isToday) {
                List<Residence> targetResidences;
                if (event.getBuildingNumber() != null) {
                    targetResidences = residenceRepository.findByBuildingNumber(event.getBuildingNumber());
                } else {
                    targetResidences = residenceRepository.findAll();
                }

                String title = "Event Reminder: " + event.getTitle();
                String body = event.getDescription();

                for (Residence residence : targetResidences) {
                    if (residence.getFcmToken() != null) {
                        Map<String, String> dataPayload = Map.of(
                                "eventId", String.valueOf(event.getId()),
                                "route", "/eventDetails"
                        );
                        try {
                            fcmService.sendNotification(residence.getFcmToken(), title, body, dataPayload);
                            logger.info("Sent notification for event '{}' to {}", event.getTitle(), residence.getFlatNumber());
                        } catch (Exception e) {
                            logger.error("Failed to send notification to {}", residence.getFlatNumber(), e);
                        }
                    }
                }
            }
        }

        logger.info("EventNotificationScheduler finished");
    }
}
