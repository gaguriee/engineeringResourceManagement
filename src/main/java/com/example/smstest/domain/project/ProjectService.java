package com.example.smstest.domain.project;

import com.example.smstest.external.license.LicenseProject;
import com.example.smstest.external.license.LicenseProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * WBS 관련 Service Implementation
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final LicenseProjectRepository licenseProjectRepository;

    /**
     * [ 특정 키워드 포함 프로젝트명 조회 ]
     * @param keyword
     * @param pageable
     * @return
     */
    public Page<Project> searchProjects(String keyword, Pageable pageable) {

        if (keyword != null) {
            String[] words = keyword.split("\\s+");
            String newKeyword = String.join("%", words);
            return projectRepository.findAllByNameContaining(newKeyword, pageable);
        }
        return projectRepository.findAllByOrderBySupportCountDesc(pageable);    }

    public Page<LicenseProject> searchLicenseProjects(String keyword, Pageable pageable) {
        if (keyword != null) {
            String[] words = keyword.split("\\s+");
            String newKeyword = String.join("%", words);
            return licenseProjectRepository.findAllByNameContaining(newKeyword, pageable);
        }
        return licenseProjectRepository.findAllOrderedByCustomPriority(pageable);
    }
}
