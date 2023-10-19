package com.example.smstest.domain.support.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SupportFilterCriteria {
    private String customerName;
    private String projectName;
    private List<Integer> teamId;
    private List<Long> productId;
    private List<Long> issueId;
    private List<Long> stateId;
    private List<Long> projectId;
    private List<Long> engineerId;
    private String taskKeyword;
    private Date startDate;
    private Date endDate;
}