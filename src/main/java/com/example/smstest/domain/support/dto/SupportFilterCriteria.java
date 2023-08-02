package com.example.smstest.domain.support.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupportFilterCriteria {
    private List<Long> customerId;
    private List<Integer> teamId;
    private List<Long> productId;
    private List<Long> issueId;
    private List<Long> stateId;
    private List<Long> engineerId;
    private String taskKeyword; // 작업제목+요약 필터링에 사용할 키워드
}