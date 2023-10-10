package com.example.smstest.domain.support.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CreateProjectRequest {

    private String clientName;

    private String projectName;

    private Long productId;

    private Integer teamId;

    private Date startDate;

    private Date finishDate;

    private String engineerName;

    private String subEngineerName;

    private String uniqueCode;

}