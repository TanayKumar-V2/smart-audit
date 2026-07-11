package com.smartaudit.orchestrator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "contract_code", columnDefinition = "TEXT")
    private String contractCode;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "report", columnDefinition = "TEXT")
    private String report;

    @Column(name = "vulnerabilities")
    private Integer vulnerabilities;
}
