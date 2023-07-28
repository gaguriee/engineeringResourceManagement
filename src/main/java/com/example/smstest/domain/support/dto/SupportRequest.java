package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.support.entity.*;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
public class SupportRequest {

    private String taskType;

    private Date supportDate;

    private String supportType;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private Long customer_id;

    private Long team_id; // 정규성 위반

    private Long product_id;

    private Long issue_id;

    private Long state_id;

    private Long engineer_id;
}
