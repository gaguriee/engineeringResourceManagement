package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.support.entity.Product;
import com.example.smstest.domain.team.entity.Team;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

}