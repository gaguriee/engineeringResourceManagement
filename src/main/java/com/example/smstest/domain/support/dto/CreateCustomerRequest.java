package com.example.smstest.domain.support.dto;

import lombok.Data;

@Data
public class CreateCustomerRequest {

    private String customerName;

    private String projectName;
}