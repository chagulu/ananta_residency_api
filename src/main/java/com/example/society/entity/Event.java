package com.example.society.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "building_number", length = 50)
    private String buildingNumber;

    @Column(name = "flat_number", length = 50)
    private String flatNumber;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "reminder_before_minutes")
    private Integer reminderBeforeMinutes = 60; // Default 1 hour before event

    @Column(name = "created_by")
    private String createdBy; //

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ===== Constructors =====
    public Event() {}

    public Event(String title, String description, String buildingNumber, String flatNumber,
                 LocalDateTime eventDate, Integer reminderBeforeMinutes, String createdBy) {
        this.title = title;
        this.description = description;
        this.buildingNumber = buildingNumber;
        this.flatNumber = flatNumber;
        this.eventDate = eventDate;
        this.reminderBeforeMinutes = reminderBeforeMinutes;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getReminderBeforeMinutes() {
        return reminderBeforeMinutes;
    }

    public void setReminderBeforeMinutes(Integer reminderBeforeMinutes) {
        this.reminderBeforeMinutes = reminderBeforeMinutes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== Lifecycle Callbacks =====
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
