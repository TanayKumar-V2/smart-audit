package com.smartaudit.orchestrator.controller;

import com.smartaudit.orchestrator.dto.AuditSubmitRequest;
import com.smartaudit.orchestrator.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audits")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitAudit(@RequestBody AuditSubmitRequest request) {
        var savedRequest = auditService.createAuditRequest(request.getProjectName(), request.getContractCode());
        return ResponseEntity.ok(savedRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAudit(@PathVariable java.util.UUID id) {
        return auditService.getAudit(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
