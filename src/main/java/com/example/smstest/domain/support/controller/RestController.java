package com.example.smstest.domain.support.controller;

import com.example.smstest.domain.support.dto.CreateCustomerRequest;
import com.example.smstest.domain.customer.dto.CustomerCreateResponse;
import com.example.smstest.domain.customer.entity.Customer;
import com.example.smstest.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RestController
public class RestController {
    private final CustomerRepository customerRepository;

    @PostMapping("/customer/create")
    public CustomerCreateResponse createCustomer(@RequestBody CreateCustomerRequest request) {
        boolean exist = customerRepository.existsByName(request.getCustomerName());
        if (exist){
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