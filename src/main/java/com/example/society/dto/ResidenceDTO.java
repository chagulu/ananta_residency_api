package com.example.society.dto;

import com.example.society.model.Residence;

public class ResidenceDTO {
    private Long id;
    private String name;
    private String mobileNo;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String buildingNumber;
    private String flatNumber;
    private Long createdById;

    // No-args constructor for Jackson/serializers
    public ResidenceDTO() {}

    // Convenient ctor from entity
    public ResidenceDTO(Residence res) {
        if (res == null) return;
        this.id = res.getId();
        this.name = res.getName();
        this.mobileNo = res.getMobileNo();
        this.address = res.getAddress();
        this.city = res.getCity();
        this.state = res.getState();
        this.pincode = res.getPincode();           // map pincode
        this.buildingNumber = res.getBuildingNumber();
        this.flatNumber = res.getFlatNumber();
        this.createdById = res.getCreatedBy() != null ? res.getCreatedBy().getId() : null;
    }

    // Getters and setters (generate via IDE or Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
}
