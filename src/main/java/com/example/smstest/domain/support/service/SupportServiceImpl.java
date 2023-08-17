package com.example.smstest.domain.support.service;

import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.support.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;
    private final IssueRepository issueRepository;

    private final StateRepository stateRepository;

    private final ProductRepository productRepository;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;
    private final SupportTypeRepository supportTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public SupportServiceImpl(SupportRepository supportRepository, IssueRepository issueRepository, StateRepository stateRepository, ProductRepository productRepository, CustomerRepository customerRepository, TeamRepository teamRepository, MempRepository mempRepository, SupportTypeRepository supportTypeRepository, PasswordEncoder passwordEncoder) {
        this.supportRepository = supportRepository;
        this.issueRepository = issueRepository;
        this.stateRepository = stateRepository;
        this.productRepository = productRepository;
        this.teamRepository = teamRepository;
        this.mempRepository = mempRepository;
        this.supportTypeRepository = supportTypeRepository;
        this.passwordEncoder = passwordEncoder;
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
        support.setCustomerName(supportRequest.getCustomerName());
        support.setSupportTypeHour(supportRequest.getSupportTypeHour());

        // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
        support.setProduct(productRepository.findById(supportRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(supportRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(supportRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findById(supportRequest.getEngineerId()).orElse(null));
        support.setTeam(teamRepository.findById(support.getEngineer().getTeamId()).orElse(null));

        support.setSupportType(supportTypeRepository.findById(supportRequest.getSupportTypeId()).orElse(null));

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(supportRequest.getPassword());

        support.setPassword(encodedPassword);

        // Support 엔티티를 저장하고 반환
        Support newsupport = supportRepository.save(support);

        return SupportResponse.entityToResponse(newsupport);
    }

    @Override
    public SupportResponse modifySupport(ModifyRequest modifyRequest) {
        Support support = supportRepository.findById(modifyRequest.getSupportId()).get();

        // SupportRequest를 Support 엔티티로 변환
        support.setSupportDate(modifyRequest.getSupportDate());
        support.setRedmineIssue(modifyRequest.getRedmineIssue());
        support.setTaskTitle(modifyRequest.getTaskTitle());
        support.setTaskSummary(modifyRequest.getTaskSummary());
        support.setTaskDetails(modifyRequest.getTaskDetails());
        support.setCustomerContact(modifyRequest.getCustomerContact());
        support.setCustomerName(modifyRequest.getCustomerName());
        support.setSupportTypeHour(modifyRequest.getSupportTypeHour());

        // ID로 Customer, Team, Product, Issue, State, Memp 엔티티들을 찾아와서 설정
        support.setProduct(productRepository.findById(modifyRequest.getProductId()).orElse(null));
        support.setIssue(issueRepository.findById(modifyRequest.getIssueId()).orElse(null));
        support.setState(stateRepository.findById(modifyRequest.getStateId()).orElse(null));
        support.setEngineer(mempRepository.findById(modifyRequest.getEngineerId()).orElse(null));
        support.setTeam(teamRepository.findById(support.getEngineer().getTeamId()).orElse(null));

        support.setSupportType(supportTypeRepository.findById(modifyRequest.getSupportTypeId()).orElse(null));

        // Support 엔티티를 저장하고 반환
        Support savedsupport = supportRepository.save(support);

        return SupportResponse.entityToResponse(savedsupport);    }


}
