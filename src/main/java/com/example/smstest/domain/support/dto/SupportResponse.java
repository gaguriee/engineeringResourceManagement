package com.example.smstest.domain.support.dto;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@Data
public class SupportResponse {

    private Long id;

    private String fileId;

    private String productName;

    private String customerName;

    private String customerContact;

    private String taskType;

    private String issueType;

    private String businessType;

    private String engineerName;

    @Temporal(TemporalType.DATE)
    private Date supportDate;

    private String supportType;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private String fileName;

    private String location;

    private String affiliation;

}
