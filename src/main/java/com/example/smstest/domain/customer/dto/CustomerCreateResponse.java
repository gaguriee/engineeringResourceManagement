package com.example.smstest.domain.customer.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CustomerCreateResponse {
    private boolean customerExist;

    public CustomerCreateResponse(boolean customerExist) {
        this.customerExist = customerExist;
    }
}