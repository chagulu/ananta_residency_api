package com.example.society.guest.repository;

import com.example.society.guest.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // ✅ Add this line
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface GuestEntryRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByToken(String token);


    Page<Visitor> findByFlatNumberAndBuildingNumber(String flatNumber, String buildingNumber, Pageable pageable);
}
