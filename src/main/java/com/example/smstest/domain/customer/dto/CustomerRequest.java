package com.example.smstest.domain.customer.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class CustomerRequest {

    private String customerName;

}
