package com.smartaudit.orchestrator.service;

import com.smartaudit.orchestrator.dto.AuditRequestEvent;
import com.smartaudit.orchestrator.model.AuditRequest;
import com.smartaudit.orchestrator.repository.AuditRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditRequestRepository repository;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    
    private static final String TOPIC = "contract-uploads";

    @Autowired
    public AuditService(AuditRequestRepository repository, KafkaTemplate<Object, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public AuditRequest createAuditRequest(String projectName, String contractCode) {
        AuditRequest request = new AuditRequest();
        request.setProjectName(projectName);
        request.setContractCode(contractCode);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        
        AuditRequest saved = repository.save(request);
        
        AuditRequestEvent event = new AuditRequestEvent(saved.getId(), saved.getProjectName(), saved.getContractCode());
        kafkaTemplate.send(TOPIC, event);
        
        return saved;
    }
}
