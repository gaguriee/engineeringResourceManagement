package com.example.smstest.domain.wbs.service;

import com.example.smstest.domain.wbs.Interface.WBSService;
import com.example.smstest.domain.wbs.entity.Project;
import com.example.smstest.domain.wbs.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * WBS 관련 Service Implementation
 */
@Service
@RequiredArgsConstructor
public class WBSServiceImpl implements WBSService {

    private final ProjectRepository projectRepository;

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
}
