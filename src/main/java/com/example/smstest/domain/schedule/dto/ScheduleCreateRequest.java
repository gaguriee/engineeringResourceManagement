package com.example.smstest.domain.schedule.dto;

import lombok.Data;

import java.sql.Date;


@Data
public class ScheduleCreateRequest {

    private String content;

    private Date date;

    private Integer teamId;

    private Long engineerId;
}