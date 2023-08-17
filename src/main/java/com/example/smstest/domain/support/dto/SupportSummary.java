package com.example.smstest.domain.support.dto;

import lombok.Data;

@Data
public class SupportSummary {
    private int year;
    private int month;
    private String product;
    private String engineer;
    private String state;
    private long count;

    public SupportSummary(int year, int month, String product, String engineer, String state, long count) {
        this.year = year;
        this.month = month;
        this.product = product;
        this.engineer = engineer;
        this.state = state;
        this.count = count;
    }

}
