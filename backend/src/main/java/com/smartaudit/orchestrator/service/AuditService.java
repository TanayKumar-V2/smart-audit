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

    public java.util.Optional<AuditRequest> getAudit(java.util.UUID id) {
        return repository.findById(id);
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "audit-results", groupId = "orchestrator-group")
    public void listenAuditResults(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> result = mapper.readValue(message, new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>(){});
            java.util.UUID auditId = java.util.UUID.fromString(result.get("auditId").toString());
            
            repository.findById(auditId).ifPresent(audit -> {
                audit.setReport(result.get("report") != null ? result.get("report").toString() : null);
                audit.setVulnerabilities(result.get("vulnerabilities") != null ? (Integer) result.get("vulnerabilities") : 0);
                audit.setStatus(result.get("status").toString());
                repository.save(audit);
                System.out.println("Updated audit " + auditId + " with results.");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
