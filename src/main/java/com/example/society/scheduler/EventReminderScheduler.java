package com.example.society.scheduler;

import com.example.society.entity.Event;
import com.example.society.model.Residence;
import com.example.society.repository.EventRepository;
import com.example.society.repository.ResidenceRepository;
import com.example.society.service.FcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
public class EventReminderScheduler {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private FcmService fcmService;

    // Run every day at 6:00 AM
    @Scheduled(cron = "0 0 6 * * *")
    // @Scheduled(cron = "0 * * * * *") // Every minute
    public void sendEventReminders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Fetch events for today or tomorrow
        List<Event> events = eventRepository.findAllByEventDateBetween(
                today.atStartOfDay(), tomorrow.atTime(LocalTime.MAX)
        );

        for (Event event : events) {
            LocalDate eventDate = event.getEventDate().toLocalDate();

            String title = "Event Reminder: " + event.getTitle();
            String body;

            if (eventDate.equals(today)) {
                body = "Reminder: Event '" + event.getTitle() + "' is today.";
            } else {
                body = "Reminder: Event '" + event.getTitle() + "' is tomorrow.";
            }

            // Determine target residents
            List<Residence> targetResidences;
            if (StringUtils.hasText(event.getBuildingNumber())) {
                targetResidences = residenceRepository.findByBuildingNumber(event.getBuildingNumber());
            } else {
                targetResidences = residenceRepository.findAll();
            }

            // Send FCM notifications
            for (Residence res : targetResidences) {
                if (StringUtils.hasText(res.getFcmToken())) {
                    Map<String, String> dataPayload = Map.of(
                            "route", "/eventDetails",
                            "eventId", String.valueOf(event.getId())
                    );
                    try {
                        fcmService.sendNotification(res.getFcmToken(), title, body, dataPayload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
