package com.example.smstest.domain.client.Interface;

import com.example.smstest.domain.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    Page<Client> searchClients(String keyword, Pageable pageable);
    Client getClientDetails(Integer customerId);
}