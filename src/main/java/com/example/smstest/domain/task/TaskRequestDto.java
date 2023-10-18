package com.example.smstest.domain.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskRequestDto {

    private Integer categoryId;

    private Date estimatedStartDate;

    private Date estimatedEndDate;

    private Date actualStartDate;

    private Date actualEndDate;

    private String actualOutput;

    private String taskName;

}
