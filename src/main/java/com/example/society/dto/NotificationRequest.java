package com.example.society.dto;

public class NotificationRequest {
    private Long flatId;
    private String title;
    private String message;
    private Long adminId;
    private String fcmToken;
    private Long userId; // ✅ Added to support saving token
    private String mobileNo; // ✅ Added to support mobile-based token registration

    // Getters and setters
    public Long getFlatId() { 
        return flatId; 
    }
    public void setFlatId(Long flatId) { 
        this.flatId = flatId; 
    }

    public String getTitle() { 
        return title; 
    }
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getMessage() { 
        return message; 
    }
    public void setMessage(String message) { 
        this.message = message; 
    }

    public Long getAdminId() { 
        return adminId; 
    }
    public void setAdminId(Long adminId) { 
        this.adminId = adminId; 
    }

    public String getFcmToken() { 
        return fcmToken; 
    }
    public void setFcmToken(String fcmToken) { 
        this.fcmToken = fcmToken; 
    }

    public Long getUserId() { 
        return userId; 
    }
    public void setUserId(Long userId) { 
        this.userId = userId; 
    }

    // ✅ Mobile number getter/setter
    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
