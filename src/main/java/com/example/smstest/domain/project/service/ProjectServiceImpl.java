package com.example.smstest.domain.project.service;

import com.example.smstest.domain.project.Interface.ProjectService;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Page<Project> searchProjects(String keyword, Pageable pageable) {
        if (keyword != null) {
            return projectRepository.findByNameContaining(keyword, pageable);
        }
        return projectRepository.findAll(pageable);    }
}
