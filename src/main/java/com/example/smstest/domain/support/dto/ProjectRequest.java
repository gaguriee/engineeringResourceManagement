package com.example.smstest.domain.support.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectRequest {

    private Long projectId;

    private Long productId;

    private Integer teamId;
    private String engineerName;

    private String subEngineerName;

    @Builder
    public ProjectRequest(Long productId, Integer teamId, String engineerName, String subEngineerName) {

        this.productId = productId;
        this.teamId = teamId;
        this.engineerName = engineerName;
        this.subEngineerName = subEngineerName;
    }
}