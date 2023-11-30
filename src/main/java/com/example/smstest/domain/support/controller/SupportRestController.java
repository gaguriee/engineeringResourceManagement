package com.example.smstest.domain.support.controller;

import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supports")
public class SupportRestController {

    private final SupportRepository supportRepository;
    private final SupportService supportService;

    @GetMapping("/all")
    public Page<Support> searchSupports(
            @RequestParam(name = "taskTitle", required = false) String taskTitle,
            Pageable pageable) {
        if (taskTitle != null) {
            return supportRepository.findByTaskTitleContaining(taskTitle, pageable);
        } else {
            return supportRepository.findAll(pageable);
        }
    }

    @GetMapping("/search")
    public Page<SupportResponse> searchSupportByFiltersApi(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) List<Integer> teamId,
            @RequestParam(required = false) List<Long> productId,
            @RequestParam(required = false) List<Long> issueId,
            @RequestParam(required = false) List<Long> stateId,
            @RequestParam(required = false) List<Long> engineerId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            Pageable pageable) {

        SupportFilterCriteria criteria = new SupportFilterCriteria();
        criteria.setCustomerName(customerName);
        criteria.setProjectName(projectName);
        criteria.setTeamId(teamId);
        criteria.setProductId(productId);
        criteria.setIssueId(issueId);
        criteria.setStateId(stateId);
        criteria.setEngineerId(engineerId);
        criteria.setTaskKeyword(keyword);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<SupportResponse> responsePage = supportService.searchSupportByFilters(criteria, pageable, sortOrder);

        return responsePage;
    }
}
