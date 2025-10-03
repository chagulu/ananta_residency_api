package com.example.society.repository;

import com.example.society.guest.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long>, JpaSpecificationExecutor<Visitor> {

    Optional<Visitor> findByToken(String token);

    // Count visitors by approval status
    long countByApproveStatus(Visitor.ApproveStatus status);

    // Count all visitors (optional, but JpaRepository already provides count())
    // long count(); // inherited from JpaRepository
}
