package com.example.smstest.domain.main;

import lombok.Data;

import java.util.Map;

@Data
public class SupportResultDTO {
    private String team;
    private Map<Long, Long> stateTotalHourMap;

    public SupportResultDTO(String team, Map<Long, Long> stateTotalHourMap) {
        this.team = team;
        this.stateTotalHourMap = stateTotalHourMap;
    }
}

