package com.example.smstest.domain.support.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProjectRequest {

    private Long projectId;

    private String clientName;

    private String projectName;

    private Long productId;

    private Integer teamId;

    private Date startDate;

    private Date finishDate;

    private String engineerName;

    private String subEngineerName;

    private String uniqueCode;

    @Builder
    public ProjectRequest(String clientName, String projectName, Long productId, Integer teamId, Date startDate, Date finishDate, String engineerName, String subEngineerName, String uniqueCode) {
        this.clientName = clientName;
        this.projectName = projectName;
        this.productId = productId;
        this.teamId = teamId;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.engineerName = engineerName;
        this.subEngineerName = subEngineerName;
        this.uniqueCode = uniqueCode;
    }
}