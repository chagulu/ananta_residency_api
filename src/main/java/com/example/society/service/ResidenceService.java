package com.example.society.service;

import com.example.society.model.Residence;
import com.example.society.repository.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidenceService {

    private final ResidenceRepository residenceRepository;

    @Autowired
    public ResidenceService(ResidenceRepository residenceRepository) {
        this.residenceRepository = residenceRepository;
    }

    public List<Residence> getAllResidences() {
        return residenceRepository.findAll();
    }

    /**
     * Returns the mobile number of the resident based on building and flat number.
     */
    public String getResidentMobile(String buildingNumber, String flatNumber) {
        return residenceRepository.findByBuildingNumberAndFlatNumber(buildingNumber, flatNumber)
                .orElseThrow(() -> new RuntimeException("Residence not found"))
                .getMobileNo();
    }


    /**
     * Returns filtered residences with pagination.
     */
    public Page<Residence> findFiltered(String name, String mobileNo, String flatNumber, String buildingNumber, Pageable pageable) {
        Specification<Residence> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (mobileNo != null && !mobileNo.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("mobileNo"), "%" + mobileNo + "%"));
        }
        if (flatNumber != null && !flatNumber.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("flatNumber"), "%" + flatNumber + "%"));
        }
        if (buildingNumber != null && !buildingNumber.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("buildingNumber"), "%" + buildingNumber + "%"));
        }

        return residenceRepository.findAll(spec, pageable);
    }
    /**
     * Fetch a residence by resident mobile number.
     */
    public Residence getByMobileNo(String mobileNo) {
        return residenceRepository.findByMobileNo(mobileNo)
                .orElseThrow(() -> new RuntimeException("Residence not found for mobile: " + mobileNo));
    }

}
