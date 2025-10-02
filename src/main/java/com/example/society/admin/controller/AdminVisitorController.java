package com.example.society.admin.controller;

import com.example.society.guest.entity.Visitor;
import com.example.society.repository.VisitorRepository;
import com.example.society.service.ResidenceService;
import com.example.society.specification.VisitorSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/visitors")
public class AdminVisitorController {

    private static final Logger logger = LoggerFactory.getLogger(AdminVisitorController.class);

    private final VisitorRepository visitorRepository;


    public AdminVisitorController(VisitorRepository visitorRepository, ResidenceService residenceService) {
        this.visitorRepository = visitorRepository;
       
    }

    @GetMapping
public ResponseEntity<Map<String, Object>> getAllVisitors(
    // other params...
    @RequestParam(required = false) String approveStatus,
    @RequestParam(defaultValue = "0") int page
) {
    Map<String, Object> response = new HashMap<>();

    Visitor.ApproveStatus statusEnum = null;
    if (approveStatus != null) {
        try {
            statusEnum = Visitor.ApproveStatus.valueOf(approveStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Invalid status value, log and optionally return empty results or error message
            logger.warn("Invalid approveStatus filter: {}", approveStatus);
            response.put("visitors", Collections.emptyList());
            response.put("currentPage", 0);
            response.put("totalPages", 0);
            response.put("totalItems", 0);
            return ResponseEntity.ok(response);
        }
    }

    // Build filters map
    Map<String, String> filters = new HashMap<>();
    // put other filters...
    if (statusEnum != null) filters.put("approveStatus", statusEnum.name());

    int adjustedPage = (page > 0) ? page - 1 : 0;
    Pageable pageable = PageRequest.of(adjustedPage, 10, Sort.by(Sort.Order.desc("createdAt")));

    Page<Visitor> result = visitorRepository.findAll(
            VisitorSpecification.getVisitorFilters(filters),
            pageable
    );

    response.put("visitors", result.getContent());
    response.put("currentPage", result.getNumber());
    response.put("totalPages", result.getTotalPages());
    response.put("totalItems", result.getTotalElements());

    return ResponseEntity.ok(response);
}


}
