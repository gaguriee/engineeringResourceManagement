package com.example.smstest.domain.support.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ModifyRequest {

    private Long supportId;

    private String taskType;

    private Date supportDate;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private String customerContact;

    private Long productId;

    private Long issueId;

    private Long stateId;

    private String customerName;

    private String projectId;

    private Long engineerId;

    private Long supportTypeId;

    private Float supportTypeHour;

    private List<Long> deletedFileId;

}
