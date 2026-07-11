package com.smartaudit.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequestEvent {
    private UUID auditId;
    private String projectName;
    private String contractCode;
}
