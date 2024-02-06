package com.example.smstest.domain.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 일정 생성 및 업데이트에 사용되는 DTO
 */
@Getter
@Setter
public class TaskRequest {

    private Integer categoryId;

    private Date estimatedStartDate;

    private Date estimatedEndDate;

    private Date actualStartDate;

    private Date actualEndDate;

    private String taskName;

    private Boolean fileDeleted;

}
