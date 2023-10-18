package com.example.smstest.domain.project.service;

import com.example.smstest.domain.project.Interface.ProjectService;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Page<Project> searchProjects(String keyword, Pageable pageable) {
        Date currentDate = new Date();

        if (keyword != null) {
            return projectRepository.findByNameContainingAndFinishDateAfter(keyword, pageable);
        }
        return projectRepository.findAllByFinishDateAfter(pageable);    }
}
