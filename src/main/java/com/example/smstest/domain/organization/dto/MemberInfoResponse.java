package com.example.smstest.domain.organization.dto;

import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.organization.entity.Department;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import lombok.Data;

import java.util.List;

/**
 * Member 페이지 Response DTO
 */
@Data
public class MemberInfoResponse {
    private List<Memp> memps;
    private Department department;
    private Memp memp;
    private Team team;
    private List<Support> supports;
    private List<Client> allClients;
}