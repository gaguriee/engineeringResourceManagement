package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByTaskTitleContainingOrTaskSummaryContainingIgnoreCase(String keyword, String keyword2);

    List<Support> findByIssueAndStateAndProductAndCustomer(Issue issue, State state, Product product, Customer customer);
    List<Support> findByEngineerId(Long engineerId);

    Long countByEngineerId(Long engineerId);

    Long countByTeamIdAndIssueId(Integer engineerId, Long issueId);
    Long countByTeamIdAndStateId(Integer engineerId, Long issueId);
    Long countByTeamIdAndProductId(Integer engineerId, Long issueId);

}