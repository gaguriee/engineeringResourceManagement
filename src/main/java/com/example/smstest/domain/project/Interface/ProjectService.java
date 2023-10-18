package com.example.smstest.domain.project.Interface;

import com.example.smstest.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Page<Project> searchProjects(String keyword, Pageable pageable);
//    Customer getCustomerDetails(Integer customerId);
//    List<SupportSummary> getSupportSummaryByCustomerId(Integer customerId);
}