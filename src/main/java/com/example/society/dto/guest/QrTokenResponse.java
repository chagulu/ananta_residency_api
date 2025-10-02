package com.example.society.dto.guest;

/**
 * Response DTO for /api/visitor/qr-token
 */
public class QrTokenResponse {
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
