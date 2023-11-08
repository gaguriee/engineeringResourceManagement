package com.example.smstest.domain.project.Interface;

import com.example.smstest.domain.project.entity.Project;
import com.example.smstest.license.entity.LicenseProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * WBS 관련 Service Implementation
 */
public interface ProjectService {

    /**
     * 특정 키워드 포함된 프로젝트명 조회
     * @param keyword
     * @param pageable
     * @return
     */
    Page<Project> searchProjects(String keyword, Pageable pageable);


    Page<LicenseProject> searchLicenseProjects(String keyword, Pageable pageable);

}