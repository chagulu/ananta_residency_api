package com.example.society.dto.guest;

/**
 * Response DTO for /api/visitor/qr-entry
 * Returned after validating the QR token and sending OTP to the guest.
 */
public class QrEntryInitResponse {
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
