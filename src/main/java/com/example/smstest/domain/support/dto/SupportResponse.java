package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.file.File;
import com.example.smstest.domain.project.Project;
import com.example.smstest.domain.support.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * 지원내역 응답 반환, 또는 로깅 시 사용될 DTO
 */
@Getter
public class SupportResponse {

    private final Long id;

    private final String productName;

    private final Project project;

    private final String issueType;

    private final Long issueId;

    private final String state;

    private final String engineerName;

    private final Long engineerId;

    private final String subEngineerName;

    private final Date supportDate;

    private final String supportType;

    private final Float supportTypeHour;

    private final String redmineIssue;

    private final String taskTitle;

    private final String taskSummary;

    private final String taskDetails;

    private final Set<File> files;

    @Builder
    public SupportResponse(Long id, String productName, Project project, String issueType, Long issueId, String state, String engineerName, Long engineerId, String subEngineerName, Date supportDate, String supportType, Float supportTypeHour, String redmineIssue, String taskTitle, String taskSummary, String taskDetails,Set<File> files) {
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
        this.files = files;
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
                .files(support.getFiles())
                .build();
    }

}
