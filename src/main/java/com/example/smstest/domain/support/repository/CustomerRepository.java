package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Add custom query methods as needed
}
