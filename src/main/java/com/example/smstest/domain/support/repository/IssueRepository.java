package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}

