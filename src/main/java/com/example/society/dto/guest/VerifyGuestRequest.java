package com.example.society.dto.guest;

/**
 * Request DTO for /api/visitor/verify-guest
 * Used to complete QR self-entry by verifying the OTP.
 */
public class VerifyGuestRequest {
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
