package com.example.smstest.domain.project.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CreateProjectResponse {

    private Long projectId;
    private boolean projectExist;

    public CreateProjectResponse(Long projectId, boolean projectExist) {
        this.projectId = projectId;
        this.projectExist = projectExist;
    }
}