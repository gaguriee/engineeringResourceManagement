package com.example.smstest.domain.organization.dto;

import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.organization.entity.Department;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import lombok.Data;

import java.util.List;

/**
 * Team 페이지 Response DTO
 */
@Data
public class TeamInfoResponse {
    private List<Memp> memps;
    private Team team;
    private Department department;
    private List<Support> supports;
}