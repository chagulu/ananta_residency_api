package com.example.society.service;

import com.example.society.entity.Event;
import com.example.society.model.Residence;
import com.example.society.repository.EventRepository;
import com.example.society.repository.ResidenceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private FcmService fcmService;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    /**
     * Admin creates an event and sends notifications to residents
     */
    public Event createEvent(Event event) {
        logger.info("Creating event: {}", event.getTitle());

        Event savedEvent = eventRepository.save(event);
        logger.info("Event saved with ID: {}", savedEvent.getId());

        List<Residence> targetResidences;

        // Target residents based on building/flat or all
        if (StringUtils.hasText(event.getBuildingNumber())) {
            targetResidences = residenceRepository.findByBuildingNumber(event.getBuildingNumber());
            logger.info("Targeting {} residents in building {}", targetResidences.size(), event.getBuildingNumber());
        } else {
            targetResidences = residenceRepository.findAll();
            logger.info("Targeting all {} residents", targetResidences.size());
        }

        // Prepare FCM notification
        String title = "New Event: " + event.getTitle();
        String body = event.getDescription() != null ? event.getDescription() : "Check the app for more details.";

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("eventId", String.valueOf(savedEvent.getId()));
        dataPayload.put("route", "/eventDetails"); // Flutter route for event details

        int successCount = 0;
        int failCount = 0;

        for (Residence res : targetResidences) {
            String token = res.getFcmToken();
            if (StringUtils.hasText(token)) {
                try {
                    fcmService.sendNotification(token, title, body, dataPayload);
                    successCount++;
                } catch (Exception ex) {
                    failCount++;
                    logger.error("Failed to send FCM notification to {} ({})", res.getMobileNo(), ex.getMessage());
                }
            }
        }

        logger.info("Event '{}' notifications sent successfully: {} success, {} failed", event.getTitle(), successCount, failCount);
        return savedEvent;
    }

    /**
     * Get all events ordered by date
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    /**
     * Get event by ID
     */
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }
}
