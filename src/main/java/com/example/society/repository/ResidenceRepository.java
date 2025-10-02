package com.example.society.repository;

import com.example.society.model.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ResidenceRepository extends JpaRepository<Residence, Long>, JpaSpecificationExecutor<Residence> {
    Optional<Residence> findByMobileNo(String mobileNo);
    Optional<Residence> findByBuildingNumberAndFlatNumber(String buildingNumber, String flatNumber);
    boolean existsByMobileNo(String mobileNo);
}
