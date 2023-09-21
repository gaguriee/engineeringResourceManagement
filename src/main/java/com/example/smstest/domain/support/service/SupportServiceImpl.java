package com.example.smstest.domain.support.service;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.repository.CustomerRepository;
import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;
    private final StateRepository stateRepository;
    private final ProductRepository productRepository;
    private final MempRepository mempRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final CustomerRepository customerRepository;
    private final ProjectRepository projectRepository;

    public Page<SupportResponse> searchSupportByFilters(
            SupportFilterCriteria criteria, Pageable pageable, String sortOrder) {
        Page<Support> result = supportRepository.searchSupportByFilters(criteria, pageable, sortOrder);
        return new PageImpl<>(
                result.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.getTotalElements());
    }
    @Override
    public SupportResponse getDetails(Long supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return SupportResponse.entityToResponse(support);
    }

    public SupportResponse createSupport(SupportRequest supportRequest) {

        Support support = new Support();

        support.setSupportDate(supportRequest.getSupportDate());
        support.setRedmineIssue(supportRequest.getRedmineIssue());
        support.setTaskTitle(supportRequest.getTaskTitle());
        support.setTaskSummary(supportRequest.getTaskSummary());
        support.setTaskDetails(supportRequest.getTaskDetails());
        support.setSupportTypeHour(supportRequest.getSupportTypeHour());

        support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findOneByName(supportRequest.getEngineerName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
        support.setProject(projectRepository.findById(supportRequest.getProjectId()).get());
        support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);
        log.info("===CREATE=== ("+ SupportResponse.entityToResponse(newsupport) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());

        return SupportResponse.entityToResponse(newsupport);
    }

    @Override
    public SupportResponse modifySupport(ModifyRequest supportRequest) {

        Support support = supportRepository.findById(supportRequest.getSupportId()).get();
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (support.getEngineer().getUsername().equals(user.getUsername())
        || user.getRole().name().equals("ADMIN")){
            support.setSupportDate(supportRequest.getSupportDate());
            support.setRedmineIssue(supportRequest.getRedmineIssue());
            support.setTaskTitle(supportRequest.getTaskTitle());
            support.setTaskSummary(supportRequest.getTaskSummary());
            support.setTaskDetails(supportRequest.getTaskDetails());
            support.setSupportTypeHour(supportRequest.getSupportTypeHour());

            support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
            support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
            support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
            support.setEngineer(mempRepository.findOneByName(supportRequest.getEngineerName())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
            support.setProject(projectRepository.findById(supportRequest.getProjectId()).get());
            support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

            // Support 엔티티를 저장하고 반환
            Support savedsupport = supportRepository.save(support);
            log.info("===MODIFY=== ("+ SupportResponse.entityToResponse(savedsupport) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());

            return SupportResponse.entityToResponse(savedsupport);
        }

        return null;
    }

    @Override
    public void deleteSupport(Long supportId) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Support support = supportRepository.findById(supportId).orElse(null);

        if (support != null && (support.getEngineer().getUsername().equals(user.getUsername())
                || user.getRole().name().equals("ADMIN"))){
            supportRepository.delete(support);
            log.info("===DELETE=== ("+ SupportResponse.entityToResponse(support) +") by "+ SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }


}
