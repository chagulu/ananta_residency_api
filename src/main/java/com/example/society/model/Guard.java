package com.example.society.model;

import jakarta.persistence.*;

@Entity
@Table(name = "guard")
public class Guard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "mobile_no", nullable = false, unique = true)
    private String mobileNo;

    @Column(nullable = true)
    private String email;

    @Column(name = "residence_name")
    private String residenceName;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "building_number")
    private String buildingNumber;

    public Guard() {}

    // Full constructor
    public Guard(String username, String password, String mobileNo, String email,
                String residenceName, String flatNumber, String buildingNumber) {
        this.username = username;
        this.password = password;
        this.mobileNo = mobileNo;
        this.email = email;
        this.residenceName = residenceName;
        this.flatNumber = flatNumber;
        this.buildingNumber = buildingNumber;
    }

    // Constructor for use-cases where residence info isn't needed
    public Guard(String username, String password, String mobileNo, String email) {
        this.username = username;
        this.password = password;
        this.mobileNo = mobileNo;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
