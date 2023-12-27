package com.example.smstest.domain.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 지원 내역 필터링 시 사용할 Criteria(기준) 객체
 */
@Getter
@Setter
@NoArgsConstructor
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