package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.customer.entity.Customer;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.team.entity.Memp;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

@Data
public class SupportResponse {

    private Long id;

    private String productName;

    private Customer customer;

    private String customerContact;

    private String issueType;

    private String state;

    private String engineerName;

    private String subEngineerName;

    private Date supportDate;

    private String supportType;

    private Integer supportTypeHour;

    private String redmineIssue;

    private String taskTitle;

    private String taskSummary;

    private String taskDetails;

    private String password;

    @Builder
    public SupportResponse(Long id, String productName, Customer customer, String customerContact, String issueType, String state, String engineerName, String subEngineerName, Date supportDate, String supportType, Integer supportTypeHour, String redmineIssue, String taskTitle, String taskSummary, String taskDetails, String password) {
        this.id = id;
        this.productName = productName;
        this.customer = customer;
        this.customerContact = customerContact;
        this.issueType = issueType;
        this.state = state;
        this.engineerName = engineerName;
        this.subEngineerName = subEngineerName;
        this.supportDate = supportDate;
        this.supportType = supportType;
        this.supportTypeHour = supportTypeHour;
        this.redmineIssue = redmineIssue;
        this.taskTitle = taskTitle;
        this.taskSummary = taskSummary;
        this.taskDetails = taskDetails;
        this.password = password;
    }


    public static SupportResponse entityToResponse(Support support){
        return SupportResponse.builder()
                .id(support.getId())
                .productName(Optional.ofNullable(support.getProduct()).map(Product::getName).orElse(null))
                .customer(support.getCustomer())
                .customerContact(support.getCustomerContact())
                .issueType(Optional.ofNullable(support.getIssue()).map(Issue::getName).orElse(null))
                .state(Optional.ofNullable(support.getState()).map(State::getName).orElse(null))
                .engineerName(Optional.ofNullable(support.getEngineer()).map(Memp::getName).orElse(null))
                .subEngineerName(support.getSubEngineerName())
                .supportDate(support.getSupportDate())
                .supportType(Optional.ofNullable(support.getSupportType()).map(SupportType::getName).orElse(null))
                .supportTypeHour(support.getSupportTypeHour())
                .redmineIssue(support.getRedmineIssue())
                .taskTitle(support.getTaskTitle())
                .taskSummary(support.getTaskSummary())
                .taskDetails(support.getTaskDetails())
                .password(support.getPassword())
                .build();
    }

}
