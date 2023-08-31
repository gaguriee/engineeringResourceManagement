package com.example.smstest.domain.support.service;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.customer.repository.CustomerRepository;
import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import com.example.smstest.domain.auth.repository.MempRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;
    private final StateRepository stateRepository;
    private final ProductRepository productRepository;
    private final MempRepository mempRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final CustomerRepository customerRepository;

    public SupportServiceImpl(SupportRepository supportRepository, IssueRepository issueRepository, StateRepository stateRepository, ProductRepository productRepository, MempRepository mempRepository, SupportTypeRepository supportTypeRepository, CustomerRepository customerRepository1) {
        this.supportRepository = supportRepository;
        this.issueRepository = issueRepository;
        this.stateRepository = stateRepository;
        this.productRepository = productRepository;
        this.mempRepository = mempRepository;
        this.supportTypeRepository = supportTypeRepository;
        this.customerRepository = customerRepository1;
    }

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
        Optional<Support> support = supportRepository.findById(supportId);
        SupportResponse supportResponse = SupportResponse.entityToResponse(support.get());

        return supportResponse;
    }

    public SupportResponse createSupport(SupportRequest supportRequest) {

        Support support = new Support();
        // SupportRequest를 Support 엔티티로 변환
        support.setSupportDate(supportRequest.getSupportDate());
        support.setRedmineIssue(supportRequest.getRedmineIssue());
        support.setTaskTitle(supportRequest.getTaskTitle());
        support.setTaskSummary(supportRequest.getTaskSummary());
        support.setTaskDetails(supportRequest.getTaskDetails());
        support.setSupportTypeHour(supportRequest.getSupportTypeHour());
        support.setSubEngineerName(supportRequest.getSubEngineerName());

        // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
        support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findOneByName(supportRequest.getEngineerName()));
        support.setCustomer(customerRepository.findOneByName(supportRequest.getCustomerName()));

        support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);
        log.info("===CREATE=== ("+ SupportResponse.entityToResponse(newsupport) +")");

        return SupportResponse.entityToResponse(newsupport);
    }

    @Override
    public SupportResponse modifySupport(ModifyRequest modifyRequest) {

        Support support = supportRepository.findById(modifyRequest.getSupportId()).get();
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (support.getEngineer().getUsername().equals(user.getUsername())
        || user.getId()==33){
            // SupportRequest를 Support 엔티티로 변환
            support.setSupportDate(modifyRequest.getSupportDate());
            support.setRedmineIssue(modifyRequest.getRedmineIssue());
            support.setTaskTitle(modifyRequest.getTaskTitle());
            support.setTaskSummary(modifyRequest.getTaskSummary());
            support.setTaskDetails(modifyRequest.getTaskDetails());
            support.setSupportTypeHour(modifyRequest.getSupportTypeHour());
            support.setSubEngineerName(modifyRequest.getSubEngineerName());

            // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
            support.setProduct(productRepository.findById(modifyRequest.getProductId()).orElse(null));
            support.setIssue(issueRepository.findById(modifyRequest.getIssueId()).orElse(null));
            support.setState(stateRepository.findById(modifyRequest.getStateId()).orElse(null));
            support.setEngineer(mempRepository.findOneByName(modifyRequest.getEngineerName()));
            support.setCustomer(customerRepository.findOneByName(modifyRequest.getCustomerName()));

            support.setSupportType(supportTypeRepository.findById(modifyRequest.getSupportTypeId()).orElse(null));

            // Support 엔티티를 저장하고 반환
            Support savedsupport = supportRepository.save(support);
            log.info("===MODIFY=== (" + savedsupport.getTaskTitle() + " :: " + savedsupport.getEngineer().getName() + ")");

            return SupportResponse.entityToResponse(savedsupport);
        }

        return null;
    }

    @Override
    public void deleteSupport(Long supportId) {
        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Support support = supportRepository.findById(supportId).orElse(null);

        if (support != null && (support.getEngineer().getUsername().equals(user.getUsername())
                || user.getId()==33)){
            supportRepository.delete(support);
            log.info("===DELETE=== (" + support.getTaskTitle() + " :: " + support.getEngineer().getName() + ")");
        }
    }


}
