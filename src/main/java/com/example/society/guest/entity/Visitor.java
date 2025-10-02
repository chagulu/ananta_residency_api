package com.example.society.guest.entity;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visitors")
@Data
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestName;
    private String mobile;
    private String flatNumber;
    private String buildingNumber;
    private String visitPurpose;

    @Column(name = "vehicle_details")
    private String vehicleDetails;

    private LocalDateTime visitTime;

    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status", nullable = false)
    private ApproveStatus approveStatus = ApproveStatus.PENDING;

    // New columns
    @Column(name = "approve_by")
    private String approveBy; // who approved or rejected

    @Column(name = "approve_at")
    private LocalDateTime approveAt; // when it was approved or rejected

    @Column(unique = true, nullable = false)
    private String token;

    @PrePersist
    public void generateToken() {
        if (this.token == null || this.token.isBlank()) {
            this.token = UUID.randomUUID().toString();
        }
        if (this.approveStatus == null) {
            this.approveStatus = ApproveStatus.PENDING;
        }
    }

    public enum ApproveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
