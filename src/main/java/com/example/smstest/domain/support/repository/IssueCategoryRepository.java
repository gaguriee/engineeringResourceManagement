package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Issue;
import com.example.smstest.domain.support.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {

    @Query("SELECT ic FROM IssueCategory ic WHERE ic.visibility = true ORDER BY ic.priority")
    List<IssueCategory> findAllOrderedByPriority();

}

