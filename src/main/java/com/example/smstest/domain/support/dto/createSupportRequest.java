package com.example.smstest.domain.support.dto;

import lombok.Data;

import java.util.Date;

@Data
public class createSupportRequest {

    private Long customerId;
    private Long teamId;
    private Long productId;
    private Long issueId;
    private Long stateId;
    private Long engineerId;
    private String customerContact;
    private Date supportDate;
    private String redmineIssue;
    private String taskTitle;
    private String taskSummary;
    private String taskDetails;


}
