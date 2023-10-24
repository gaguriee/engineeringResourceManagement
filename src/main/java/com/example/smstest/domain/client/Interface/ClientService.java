package com.example.smstest.domain.client.Interface;

import com.example.smstest.domain.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 고객사 상호작용과 관련된 Service Interface
 * METHOD : 고객사 검색, 고객사 상세정보 가져오기
 */
public interface ClientService {
    Page<Client> searchClients(String keyword, Pageable pageable);
    Client getClientDetails(Integer customerId);
}