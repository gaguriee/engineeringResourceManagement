package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, String> {
    List<Support> findByTitleContainingOrSummaryContainingIgnoreCase(String keyword, String keyword2);

}