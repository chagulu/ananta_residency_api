package com.example.society.admin.repository;

import com.example.society.admin.entity.SubAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubAdminRepository extends JpaRepository<SubAdmin, Long> {
    // You can add custom queries if needed
    SubAdmin findByUsername(String username);
}
