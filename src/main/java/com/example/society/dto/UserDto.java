package com.example.society.dto;

public class UserDto {
    private String username;
    private String password;
    private String mobileNo;
    private String email;

    // New residence fields
    private String residenceName;
    private String flatNumber;
    private String buildingNumber;

    public UserDto() {}

    public UserDto(String username, String password, String mobileNo, String email,
                   String residenceName, String flatNumber, String buildingNumber) {
        this.username = username;
        this.password = password;
        this.mobileNo = mobileNo;
        this.email = email;
        this.residenceName = residenceName;
        this.flatNumber = flatNumber;
        this.buildingNumber = buildingNumber;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResidenceName() { return residenceName; }
    public void setResidenceName(String residenceName) { this.residenceName = residenceName; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }
}
