package com.example.smstest.domain.support.dto;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class SupportResponse {

    private Integer supportId;

    private String product;

    private String customer;

    private String manager;

    private String task;

    private String issue;

    private String state;

    private String engineer;

    private Date supportAt;

    private String type;

    private String redmine;

    private String title;

    private String summary;

    private String details;

    private String fileName;

    private String location;

    private String team;


}
