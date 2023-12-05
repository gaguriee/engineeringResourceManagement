package com.example.smstest.domain.client.service;

import com.example.smstest.domain.client.entity.Client;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 고객사 페이지와 관련된 Service Implements
 * METHOD : 고객사 검색, 고객사 상세정보 가져오기
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * 고객사 검색
     * @param keyword 고객사 검색 키워드
     * @param pageable
     * @return Page<Client>
     */
    public Page<Client> searchClients(String keyword, Pageable pageable) {
        if (keyword != null) {
            return clientRepository.findByNameContainingOrderBySupportCountDesc(keyword, pageable);
        }
        return clientRepository.findAllBySupportCount(pageable);
    }

    /**
     * 고객사 상세정보 가져오기
     * @param customerId
     * @return
     */
    public Client getClientDetails(Integer customerId) {
        return clientRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
    }

}
