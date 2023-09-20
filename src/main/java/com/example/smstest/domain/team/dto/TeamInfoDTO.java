package com.example.smstest.domain.team.dto;

import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.team.entity.Department;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.team.entity.Team;
import lombok.Data;

import java.util.List;

@Data
public class TeamInfoDTO {
    private List<Memp> memps;
    private Team team;
    private Department department;
    private List<Support> supports;
}