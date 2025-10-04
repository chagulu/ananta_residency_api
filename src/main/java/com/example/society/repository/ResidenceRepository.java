package com.example.society.repository;

import com.example.society.model.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ResidenceRepository extends JpaRepository<Residence, Long>, JpaSpecificationExecutor<Residence> {

    Optional<Residence> findByMobileNo(String mobileNo);

    Optional<Residence> findByBuildingNumberAndFlatNumber(String buildingNumber, String flatNumber);

    boolean existsByMobileNo(String mobileNo);

    // ✅ Add this method to fetch all residences in a building
    List<Residence> findByBuildingNumber(String buildingNumber);
    
    // Optional: fetch only FCM tokens for active residents in a building
    // @Query("SELECT r.fcmToken FROM Residence r WHERE r.buildingNumber = :buildingNumber AND r.fcmToken IS NOT NULL")
    // List<String> findFcmTokensByBuilding(@Param("buildingNumber") String buildingNumber);
}
