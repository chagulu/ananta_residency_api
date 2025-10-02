package com.example.society.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Map;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    @Value("${msg91.authkey}")
    private String authKey;

    @Value("${msg91.senderId}")
    private String senderId;

    // If true, always log OTP and allow using generated OTP when not provided
    @Value("${otp.log.enabled:true}")
    private boolean logOtpEnabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SecureRandom secureRandom = new SecureRandom();

    private String generateOtp() {
        int n = secureRandom.nextInt(1_000_000); // 0..999999
        return String.format("%06d", n);
    }

    public boolean sendOtp(String mobileNo, String otp) {
        try {
            String fullMobile = "91" + mobileNo; // Add country code without '+'

            // If caller passes null/blank OTP (e.g., guest flow), generate one for testing
            String finalOtp = (otp == null || otp.isBlank()) ? generateOtp() : otp;

            // Log OTP for testing purposes
            if (logOtpEnabled) {
                log.info("TEST OTP for {}: {}", mobileNo, finalOtp);
            }

            URI uri = UriComponentsBuilder.fromUriString("https://api.msg91.com/api/v5/otp")
                    .queryParam("template_id", "68087679d6fc054ae311ddc8")
                    .queryParam("mobile", fullMobile)
                    .queryParam("authkey", authKey) // MSG91 supports authkey as query for send OTP
                    .queryParam("otp", finalOtp)
                    .queryParam("sender", senderId)
                    .build()
                    .toUri();

            Map response = restTemplate.getForObject(uri, Map.class);
            log.debug("MSG91 OTP response: {}", response);
            return true;
        } catch (Exception e) {
            log.error("Failed to send OTP for {}", mobileNo, e);
            return false;
        }
    }

    /**
     * Verify OTP with MSG91 Verify API.
     * MSG91 expects authkey in header and mobile with country code in query string.
     */
    public boolean verifyOtp(String mobileNo, String otp) {
        try {
            String fullMobile = "91" + mobileNo;

            URI uri = UriComponentsBuilder.fromUriString("https://control.msg91.com/api/v5/otp/verify")
                    .queryParam("mobile", fullMobile)
                    .queryParam("otp", otp)
                    .build()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", authKey);
            headers.set("accept", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> resp = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
            Map body = resp.getBody();
            log.debug("MSG91 Verify OTP response: {}", body);

            if (body == null) return false;
            Object type = body.get("type");
            Object message = body.get("message");
            return "success".equalsIgnoreCase(String.valueOf(type))
                    || (message != null && String.valueOf(message).toLowerCase().contains("otp verified"));
        } catch (Exception e) {
            log.error("Failed to verify OTP for {}", mobileNo, e);
            return false;
        }
    }

    /**
     * Send approval link SMS to resident using MSG91 API.
     * Note: Many MSG91 templates require POST with JSON variables; this is illustrative.
     */
    public boolean sendApprovalLink(String mobileNo, String approvalLink) {
        try {
            String fullMobile = "91" + mobileNo;

            String templateId = "YOUR_APPROVAL_LINK_TEMPLATE_ID";

            URI uri = UriComponentsBuilder.fromUriString("https://api.msg91.com/api/v5/otp")
                    .queryParam("template_id", templateId)
                    .queryParam("mobile", fullMobile)
                    .queryParam("authkey", authKey)
                    .queryParam("sender", senderId)
                    .queryParam("approval_link", approvalLink) // must match the template var name
                    .build()
                    .toUri();

            Map response = restTemplate.getForObject(uri, Map.class);
            log.debug("MSG91 approval link response: {}", response);
            return true;
        } catch (Exception e) {
            log.error("Failed to send approval link to {}", mobileNo, e);
            return false;
        }
    }
}
