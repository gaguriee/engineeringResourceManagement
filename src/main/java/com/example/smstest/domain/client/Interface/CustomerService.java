package com.example.smstest.domain.client.Interface;

import com.example.smstest.domain.client.entity.Customer;
import com.example.smstest.domain.support.dto.SupportSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    Page<Customer> searchCustomers(String keyword, Pageable pageable);
    Customer getCustomerDetails(Integer customerId);
    List<SupportSummary> getSupportSummaryByCustomerId(Integer customerId);
}