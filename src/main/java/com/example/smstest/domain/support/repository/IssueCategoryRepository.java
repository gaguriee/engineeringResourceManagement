package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Issue;
import com.example.smstest.domain.support.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {
    // Add custom query methods as needed
}

