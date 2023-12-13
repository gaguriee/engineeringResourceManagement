package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {

    // 본부별 이슈 분류 가져오기
    @Query("SELECT ic FROM IssueCategory ic WHERE ic.visibility = true AND (ic.divisionId is null OR ic.divisionId = :divisionId) ORDER BY ic.priority")
    List<IssueCategory> findAllByDivisionIdOrderedByPriority(@Param("divisionId") Integer divisionId);

    @Query("SELECT ic FROM IssueCategory ic WHERE ic.visibility = true ORDER BY ic.priority")
    List<IssueCategory> findAllOrderedByPriority();
}

