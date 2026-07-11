package com.smartaudit.orchestrator.dto;

import lombok.Data;

@Data
public class AuditSubmitRequest {
    private String projectName;
    private String contractCode;
}
