package com.example.smstest.domain.support.service;

import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.mapper.SupportMapper;
import com.example.smstest.domain.support.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.smstest.domain.support.mapper.SupportMapper.INSTANCE;

@Service
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;

    private final StateRepository stateRepository;

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final MempRepository mempRepository;

    public SupportServiceImpl(SupportRepository supportRepository, IssueRepository issueRepository, StateRepository stateRepository, ProductRepository productRepository, CustomerRepository customerRepository, MempRepository mempRepository) {
        this.supportRepository = supportRepository;
        this.issueRepository = issueRepository;
        this.stateRepository = stateRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.mempRepository = mempRepository;
    }

    @Override
    public List<SupportResponse> searchSupports(String keyword) {
        List<Support> supports = supportRepository.findByTaskTitleContainingOrTaskSummaryContainingIgnoreCase(keyword, keyword);

        return supports.stream()
                .map(INSTANCE::entityToResponse)
                .collect(Collectors.toList());
    }

    public Page<Support> searchSupportByFilters(SupportFilterCriteria criteria, Pageable pageable) {
        return supportRepository.searchSupportByFilters(criteria, pageable);
    }

    @Override
    public SupportResponse getDetails(Long supportId) {
        Optional<Support> support = supportRepository.findById(supportId);
        SupportResponse supportResponse = INSTANCE.entityToResponse(support.get());
        return supportResponse;
    }

//    public List<SupportResponse> getFilteredPosts(Long issueId, Long stateId, Long productId, Long customerId) {
//
//        Issue issue = issueRepository.findById(issueId).orElse(null);
//        State state = stateRepository.findById(stateId).orElse(null);
//        Product product = productRepository.findById(productId).orElse(null);
//        Customer customer = customerRepository.findById(customerId).orElse(null);
//
//        List<Support> supports = supportRepository.findByIssueAndStateAndProductAndCustomer(issue, state, product, customer);
//
//        return supports.stream()
//                .map(INSTANCE::entityToResponse)
//                .collect(Collectors.toList());
//    }

    public SupportResponse createSupport(SupportRequest supportRequest) {

        Support support = new Support();
        // SupportRequest를 Support 엔티티로 변환
        support.setTaskType(supportRequest.getTaskType());
        support.setSupportDate(supportRequest.getSupportDate());
        support.setSupportType(supportRequest.getSupportType());
        support.setRedmineIssue(supportRequest.getRedmineIssue());
        support.setTaskTitle(supportRequest.getTaskTitle());
        support.setTaskSummary(supportRequest.getTaskSummary());
        support.setTaskDetails(supportRequest.getTaskDetails());

        // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
        support.setCustomer(customerRepository.findById(supportRequest.getCustomer_id()).orElse(null));
        support.setProduct(productRepository.findById(supportRequest.getProduct_id()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssue_id()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getState_id()).orElse(null));
        support.setEngineer(mempRepository.findById(supportRequest.getEngineer_id()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);

        return INSTANCE.entityToResponse(newsupport);
    }


}
