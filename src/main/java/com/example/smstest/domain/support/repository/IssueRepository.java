package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    // Add custom query methods as needed
}

