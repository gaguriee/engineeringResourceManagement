package com.example.smstest.domain.support.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class ModifyRequest {

    private Long supportId;

    private Date supportDate;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private String customerContact;

    private Long customerId;

    private Long productId;

    private Long issueId;

    private Long stateId;

    private Long engineerId;

    private String subEngineerName;

    private Long supportTypeId;

    private Integer supportTypeHour;

}
