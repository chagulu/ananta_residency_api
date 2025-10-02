package com.example.society.dto;

import java.time.LocalDateTime;

/**
 * DTOs for Visitor QR self-entry flow.
 * Keep pure data here; no business logic.
 */
public class QrDtos {

    // Response for /api/visitor/qr-token
    public static class QrTokenResponse {
        private boolean success;
        private String message;
        private String qrToken;
        private long expiresInSeconds;

        public QrTokenResponse() {}

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }

        public long getExpiresInSeconds() { return expiresInSeconds; }
        public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
    }

    // Request for /api/visitor/qr-entry
    public static class QrEntryRequest {
        private String qrToken;
        private String mobile;
        private String guestName;
        private String flatNumber;
        private String buildingNumber;
        private String visitPurpose;
        private String vehicleDetails;
        private String idempotencyKey; // optional client-provided key for de-dupe

        public QrEntryRequest() {}

        public String getQrToken() { return qrToken; }
        public void setQrToken(String qrToken) { this.qrToken = qrToken; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getGuestName() { return guestName; }
        public void setGuestName(String guestName) { this.guestName = guestName; }

        public String getFlatNumber() { return flatNumber; }
        public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

        public String getBuildingNumber() { return buildingNumber; }
        public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

        public String getVisitPurpose() { return visitPurpose; }
        public void setVisitPurpose(String visitPurpose) { this.visitPurpose = visitPurpose; }

        public String getVehicleDetails() { return vehicleDetails; }
        public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

        public String getIdempotencyKey() { return idempotencyKey; }
        public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    }

    // Response for /api/visitor/qr-entry
    public static class QrEntryInitResponse {
        private boolean success;
        private String message;
        private String correlationId;
        private String otpChannel;

        public QrEntryInitResponse() {}

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getCorrelationId() { return correlationId; }
        public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

        public String getOtpChannel() { return otpChannel; }
        public void setOtpChannel(String otpChannel) { this.otpChannel = otpChannel; }
    }

    // Request for /api/visitor/verify-guest
    public static class VerifyGuestRequest {
        private String correlationId;
        private String mobile;
        private String otpCode;

        public VerifyGuestRequest() {}

        public String getCorrelationId() { return correlationId; }
        public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    }

    // Internal pending payload (not exposed externally unless desired)
    public static class PendingGuestPayload {
        private String correlationId;
        private String mobile;
        private String guestName;
        private String flatNumber;
        private String buildingNumber;
        private String visitPurpose;
        private String vehicleDetails;
        private String createdBy;        // "QR"
        private String idempotencyKey;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt; // e.g., now + 10 minutes

        public PendingGuestPayload() {}

        public String getCorrelationId() { return correlationId; }
        public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }

        public String getGuestName() { return guestName; }
        public void setGuestName(String guestName) { this.guestName = guestName; }

        public String getFlatNumber() { return flatNumber; }
        public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

        public String getBuildingNumber() { return buildingNumber; }
        public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

        public String getVisitPurpose() { return visitPurpose; }
        public void setVisitPurpose(String visitPurpose) { this.visitPurpose = visitPurpose; }

        public String getVehicleDetails() { return vehicleDetails; }
        public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public String getIdempotencyKey() { return idempotencyKey; }
        public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }
}
