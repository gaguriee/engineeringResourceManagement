package com.example.smstest.domain.project.service;

import com.example.smstest.domain.project.Interface.ProjectService;
import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.license.entity.LicenseProject;
import com.example.smstest.license.repository.LicenseProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * WBS 관련 Service Implementation
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LicenseProjectRepository licenseProjectRepository;

    /**
     * 특정 키워드 포함 프로젝트명 조회
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<Project> searchProjects(String keyword, Pageable pageable) {

        if (keyword != null) {
            return projectRepository.findAllByNameContaining(keyword, pageable);
        }
        return projectRepository.findAllOrderedByUniqueCodeDesc(pageable);    }

    @Override
    public Page<LicenseProject> searchLicenseProjects(String keyword, Pageable pageable) {
        if (keyword != null) {
            return licenseProjectRepository.findAllByNameContaining(keyword, pageable);
        }
        return licenseProjectRepository.findAllOrderedByUniqueCodeDesc(pageable);
    }
}
