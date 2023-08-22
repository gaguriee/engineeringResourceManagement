package com.example.smstest.domain.support.controller;

import com.example.smstest.domain.support.dto.CreateCustomerRequest;
import com.example.smstest.domain.support.dto.CustomerCreateResponse;
import com.example.smstest.domain.support.dto.PasswordComparisonRequest;
import com.example.smstest.domain.support.dto.PasswordMatchResponse;
import com.example.smstest.domain.support.entity.Customer;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.CustomerRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private SupportRepository supportRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/compare-password")
    public PasswordMatchResponse comparePassword(@RequestBody PasswordComparisonRequest request) {
        Support support = supportRepository.findById(request.getSupportId()).orElse(null);

        if (support != null) {
            boolean passwordMatch = passwordEncoder.matches(request.getEnteredPassword(), support.getPassword());
            return new PasswordMatchResponse(passwordMatch);
        }

        return new PasswordMatchResponse(false);
    }

    @PostMapping("/customer/create")
    public CustomerCreateResponse createCustomer(@RequestBody CreateCustomerRequest request) {
        boolean exist = customerRepository.existsByName(request.getCustomerName());
        if (exist){
            System.out.println("EXIST");
            return new CustomerCreateResponse(true);
        }
        else {
            Customer customer = new Customer();
            customer.setName(request.getCustomerName());
            customer.setProject(request.getProjectName());
            customerRepository.save(customer);
        }

        return new CustomerCreateResponse(false);
    }
}