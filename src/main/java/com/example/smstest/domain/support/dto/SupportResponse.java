package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.auth.entity.Memp;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

@Data
public class SupportResponse {

    private Long id;

    private String productName;

    private Project project;

    private String issueType;

    private Long issueId;

    private String state;

    private String engineerName;

    private Long engineerId;

    private String subEngineerName;

    private Date supportDate;

    private String supportType;

    private Float supportTypeHour;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    @Builder
    public SupportResponse(Long id, String productName, Project project, String issueType, Long issueId, String state, String engineerName, Long engineerId, String subEngineerName, Date supportDate, String supportType, Float supportTypeHour, String redmineIssue, String taskTitle, String taskSummary, String taskDetails) {
        this.id = id;
        this.productName = productName;
        this.project = project;
        this.issueType = issueType;
        this.issueId = issueId;
        this.state = state;
        this.engineerName = engineerName;
        this.engineerId = engineerId;
        this.subEngineerName = subEngineerName;
        this.supportDate = supportDate;
        this.supportType = supportType;
        this.supportTypeHour = supportTypeHour;
        this.redmineIssue = redmineIssue;
        this.taskTitle = taskTitle;
        this.taskSummary = taskSummary;
        this.taskDetails = taskDetails;
    }


    public static SupportResponse entityToResponse(Support support){
        return SupportResponse.builder()
                .id(support.getId())
                .productName(Optional.ofNullable(support.getProduct()).map(Product::getName).orElse(null))
                .project(support.getProject())
                .issueType(Optional.ofNullable(support.getIssue()).map(Issue::getName).orElse(null))
                .issueId(Optional.ofNullable(support.getIssue()).map(Issue::getId).orElse(null))
                .state(Optional.ofNullable(support.getState()).map(State::getName).orElse(null))
                .engineerName(Optional.ofNullable(support.getEngineer()).map(Memp::getName).orElse(null))
                .engineerId(Optional.ofNullable(support.getEngineer()).map(Memp::getId).orElse(null))
                .subEngineerName(support.getSubEngineerName())
                .supportDate(support.getSupportDate())
                .supportType(Optional.ofNullable(support.getSupportType()).map(SupportType::getName).orElse(null))
                .supportTypeHour(support.getSupportTypeHour())
                .redmineIssue(support.getRedmineIssue())
                .taskTitle(support.getTaskTitle())
                .taskSummary(support.getTaskSummary())
                .taskDetails(support.getTaskDetails())
                .build();
    }

}
