package com.example.smstest.domain.support.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 지원내역 등록, 수정 시 사용될 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class SupportRequest {

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

    private String project;

    private Long engineerId;

    private Long supportTypeId;

    private Float supportTypeHour;

    /**
     * 지원내역 수정 시 사용할 필드
     */
    private Long supportId;
    private List<Long> deletedFileId;

}
