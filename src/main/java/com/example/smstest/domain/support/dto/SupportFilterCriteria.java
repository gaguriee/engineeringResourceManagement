package com.example.smstest.domain.support.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class SupportFilterCriteria {
    private List<Long> customerId;
    private List<Integer> teamId;
    private List<Long> productId;
    private List<Long> issueId;
    private List<Long> stateId;
    private List<Long> engineerId;
    private String taskKeyword;
    private Date startDate;
    private Date endDate;
}