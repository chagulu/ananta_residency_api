package com.example.society.guest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for capturing guest visit details.
 * Validated by Spring (@Valid) in controller methods.
 */
@Data
public class GuestVisitRequest {

    @NotBlank(message = "Guest name is mandatory")
    @Size(max = 100, message = "Guest name cannot exceed 100 characters")
    private String guestName;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobile;

    private String flatNumber;

    private String buildingNumber;

    private String visitPurpose;

    private String vehicleDetails;
}
