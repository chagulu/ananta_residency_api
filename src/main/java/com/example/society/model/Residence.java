package com.example.society.model;

import jakarta.persistence.*;

@Entity
@Table(name = "residences")
public class Residence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Resident's full name

    @Column(nullable = false, length = 15, unique = true)
    private String mobileNo;

    @Column(name = "building_number", nullable = false)
    private String buildingNumber;

    @Column(name = "flat_number", nullable = false)
    private String flatNumber;

    @Column(name = "total_members")
    private Integer totalMembers;

    @Column(name = "vehicle_details")
    private String vehicleDetails;

    @Column(length = 10)
    private String pincode;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;   // ✅ new field for push notifications

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Guard createdBy;

    // --- Constructors ---
    public Residence() {}

    public Residence(String name, String mobileNo, String buildingNumber, String flatNumber,
                     Integer totalMembers, String vehicleDetails, String address, String city,
                     String state, Guard createdBy) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.buildingNumber = buildingNumber;
        this.flatNumber = flatNumber;
        this.totalMembers = totalMembers;
        this.vehicleDetails = vehicleDetails;
        this.address = address;
        this.city = city;
        this.state = state;
        this.createdBy = createdBy;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }

    public String getVehicleDetails() { return vehicleDetails; }
    public void setVehicleDetails(String vehicleDetails) { this.vehicleDetails = vehicleDetails; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public Guard getCreatedBy() { return createdBy; }
    public void setCreatedBy(Guard createdBy) { this.createdBy = createdBy; }
}
