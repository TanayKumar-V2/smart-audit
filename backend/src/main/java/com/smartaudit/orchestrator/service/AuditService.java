package com.smartaudit.orchestrator.service;

import com.smartaudit.orchestrator.model.AuditRequest;
import com.smartaudit.orchestrator.repository.AuditRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditRequestRepository repository;

    @Autowired
    public AuditService(AuditRequestRepository repository) {
        this.repository = repository;
    }

    public AuditRequest createAuditRequest(String projectName, String contractCode) {
        AuditRequest request = new AuditRequest();
        request.setProjectName(projectName);
        request.setContractCode(contractCode);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        
        return repository.save(request);
    }
}
