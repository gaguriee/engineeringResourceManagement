package com.example.smstest.domain.support.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class SupportRequest {

    private String taskType;

    private Date supportDate;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private String customerContact;

    private String customerName;

    private Long productId;

    private Long issueId;

    private Long stateId;

    private Long engineerId;

    private String subEngineerName;

    private Long supportTypeId;

    private Integer supportTypeHour;

}
