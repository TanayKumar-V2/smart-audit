package com.smartaudit.orchestrator.repository;

import com.smartaudit.orchestrator.model.AuditRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditRequestRepository extends JpaRepository<AuditRequest, UUID> {
}
