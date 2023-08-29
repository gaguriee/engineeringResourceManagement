package com.example.smstest.domain.customer.service;

import com.example.smstest.domain.customer.Interface.CustomerService;
import com.example.smstest.domain.customer.entity.Customer;
import com.example.smstest.domain.customer.repository.CustomerRepository;
import com.example.smstest.domain.support.dto.SupportSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<Customer> searchCustomers(String keyword, Pageable pageable) {
        if (keyword != null) {
            return customerRepository.findByNameContainingOrderBySupportCountDesc(keyword, pageable);
        }
        return customerRepository.findAllBySupportCount(pageable);
    }

    @Override
    public Customer getCustomerDetails(Integer customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    @Override
    public List<SupportSummary> getSupportSummaryByCustomerId(Integer customerId) {
        return customerRepository.getSupportSummaryByCustomerId(customerId);
    }
}
