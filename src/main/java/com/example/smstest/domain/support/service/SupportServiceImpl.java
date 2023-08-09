package com.example.smstest.domain.support.service;

import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;

    private final StateRepository stateRepository;

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final MempRepository mempRepository;
    private final SupportTypeRepository supportTypeRepository;

    public SupportServiceImpl(SupportRepository supportRepository, IssueRepository issueRepository, StateRepository stateRepository, ProductRepository productRepository, CustomerRepository customerRepository, MempRepository mempRepository, SupportTypeRepository supportTypeRepository) {
        this.supportRepository = supportRepository;
        this.issueRepository = issueRepository;
        this.stateRepository = stateRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.mempRepository = mempRepository;
        this.supportTypeRepository = supportTypeRepository;
    }

    public Page<Support> searchSupportByFilters(SupportFilterCriteria criteria, Pageable pageable, String sort) {
        return supportRepository.searchSupportByFilters(criteria, pageable, sort);
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
        support.setCustomerContact(supportRequest.getCustomerContact());

        // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
        support.setCustomer(customerRepository.findById(supportRequest.getCustomerId()).orElse(null));
        support.setCustomerName(support.getCustomer().getName());
        support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findById(supportRequest.getEngineerId()).orElse(null));

        support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);

        return SupportResponse.entityToResponse(newsupport);
    }


}
