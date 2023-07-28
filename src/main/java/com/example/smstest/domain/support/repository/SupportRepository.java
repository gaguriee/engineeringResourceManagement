package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, String> {
    List<Support> findByTaskTitleContainingOrTaskSummaryContainingIgnoreCase(String keyword, String keyword2);

    List<Support> findByIssueAndStateAndProductAndCustomer(Issue issue, State state, Product product, Customer customer);

}