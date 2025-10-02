// LoginLogRepository.java
package com.example.society.admin.repository;

import com.example.society.admin.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}