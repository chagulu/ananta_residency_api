package com.example.society.controller;

import com.example.society.dto.ResidenceDTO;
import com.example.society.model.Residence;
import com.example.society.service.ResidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/residences")
public class ResidenceController {

    private final ResidenceService residenceService;

    @Autowired
    public ResidenceController(ResidenceService residenceService) {
        this.residenceService = residenceService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ResidenceDTO>> getResidences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(required = false) String flatNumber,
            @RequestParam(required = false) String buildingNumber
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Residence> residencesPage = residenceService.findFiltered(name, mobileNo, flatNumber, buildingNumber, pageable);

            // Convert Residence entities to DTOs while preserving pagination metadata
            Page<ResidenceDTO> dtoPage = residencesPage.map(ResidenceDTO::new);

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Page.empty());
        }
    }
}
