package com.example.society.admin.controller;

import com.example.society.dto.ResidenceDTO;
import com.example.society.dto.ResidenceRegisterRequest;
import com.example.society.model.Residence;
import com.example.society.service.AuthService;
import com.example.society.service.ResidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Import the @Valid annotation

@RestController
@RequestMapping("/api/admin/residences")
public class AdminResidenceController {

    private final ResidenceService residenceService;
    private final AuthService authService;

    @Autowired
    public AdminResidenceController(ResidenceService residenceService, AuthService authService) {
        this.residenceService = residenceService;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ResidenceDTO>> getFilteredResidences(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(required = false) String flatNumber,
            @RequestParam(required = false) String buildingNumber,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<Residence> residences = residenceService.findFiltered(name, mobileNo, flatNumber, buildingNumber, pageable);
            Page<ResidenceDTO> residenceDTOs = residences.map(ResidenceDTO::new);
            return ResponseEntity.ok(residenceDTOs);
        } catch (Exception e) {
            System.err.println("Error fetching residences: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-resident")
    public ResponseEntity<ResidenceDTO> registerResident(@Valid @RequestBody ResidenceRegisterRequest request) { // <--- ADDED @Valid HERE
        Residence registeredResidence = authService.registerResident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResidenceDTO(registeredResidence));
    }
}