package com.example.smstest.domain.support.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectRequest {

    private Long projectId;

    private String clientName;

    private String projectName;

    private Long productId;

    private Integer teamId;
    private String engineerName;

    private String subEngineerName;

    private String uniqueCode;

    @Builder
    public ProjectRequest(String clientName, String projectName, Long productId, Integer teamId, String engineerName, String subEngineerName, String uniqueCode) {
        this.clientName = clientName;
        this.projectName = projectName;
        this.productId = productId;
        this.teamId = teamId;
        this.engineerName = engineerName;
        this.subEngineerName = subEngineerName;
        this.uniqueCode = uniqueCode;
    }
}