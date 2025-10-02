package com.example.society.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResidenceRegisterRequest {
    // --- FIELDS ---
    @NotBlank(message = "Password cannot be empty") // Keeping password mandatory for security
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be 10 digits and start with 6, 7, 8, or 9")
    private String mobileNo;

    // Email is optional here (no @NotBlank), but if provided, it must be a valid format.
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Resident name cannot be empty")
    @Size(max = 255, message = "Resident name cannot exceed 255 characters")
    private String residenceName; // Resident's full name (will be used as User's username)

    // Building number and Flat number are kept mandatory as they are core to a residence
    @NotBlank(message = "Building number cannot be empty")
    @Size(max = 50, message = "Building number cannot exceed 50 characters")
    private String buildingNumber;

    @NotBlank(message = "Flat number cannot be empty")
    @Size(max = 50, message = "Flat number cannot exceed 50 characters")
    private String flatNumber;

    // --- OPTIONAL FIELDS (removed @NotBlank / @NotNull) ---
    @Size(max = 255, message = "Address cannot exceed 255 characters") // Optional, but limit length
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters") // Optional, but limit length
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters") // Optional, but limit length
    private String state;

    // totalMembers is now optional (removed @NotNull). If it's sent as null, it will be null.
    private Integer totalMembers;

    @Size(max = 255, message = "Vehicle details cannot exceed 255 characters") // Optional, but limit length
    private String vehicleDetails;


    // --- CONSTRUCTORS ---
    public ResidenceRegisterRequest() { }

    public ResidenceRegisterRequest(
            String password,
            String mobileNo,
            String email,
            String residenceName,
            String address,
            String city,
            String state,
            String buildingNumber,
            String flatNumber,
            Integer totalMembers,
            String vehicleDetails
    ) {
        this.password = password;
        this.mobileNo = mobileNo;
        this.email = email;
        this.residenceName = residenceName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.buildingNumber = buildingNumber;
        this.flatNumber = flatNumber;
        this.totalMembers = totalMembers;
        this.vehicleDetails = vehicleDetails;
    }

    // --- GETTERS AND SETTERS ---
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResidenceName() { return residenceName; }
    public void setResidenceName(String residenceName) { this.residenceName = residenceName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }

    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }
}