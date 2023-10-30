package com.example.smstest.domain.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskRequestDTO {

    private Integer categoryId;

    private Date estimatedStartDate;

    private Date estimatedEndDate;

    private Date actualStartDate;

    private Date actualEndDate;

    private String actualOutput;

    private String taskName;

}
