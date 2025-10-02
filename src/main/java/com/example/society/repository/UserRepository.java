package com.example.society.repository;

import com.example.society.model.Guard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Guard, Long> {
    Optional<Guard> findByUsername(String username);
    Optional<Guard> findByMobileNo(String mobileNo);
    boolean existsByUsername(String username);
    boolean existsByMobileNo(String mobileNo);
}