package com.example.smstest.domain.support.dto;

import lombok.Data;

@Data
public class PasswordComparisonRequest {

    private Long supportId;

    private String enteredPassword;

}