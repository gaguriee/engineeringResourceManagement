package com.example.smstest.domain.client.service;

import com.example.smstest.domain.client.Interface.CustomerService;
import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.support.dto.SupportSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final ClientRepository clientRepository;

    @Override
    public Page<Client> searchCustomers(String keyword, Pageable pageable) {
        if (keyword != null) {
            return clientRepository.findByNameContainingOrderBySupportCountDesc(keyword, pageable);
        }
        return clientRepository.findAllBySupportCount(pageable);
    }

    @Override
    public Client getCustomerDetails(Integer customerId) {
        return clientRepository.findById(customerId).orElse(null);
    }

    @Override
    public List<SupportSummary> getSupportSummaryByCustomerId(Integer customerId) {
        return clientRepository.getSupportSummaryByCustomerId(customerId);
    }
}
