package com.example.society.dto;

public class OtpRequest {
    private String mobileNo;
    private String otp;

    // Getters and Setters

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
