package com.example.smstest.domain.client;

import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
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
     * [ 고객사 검색 ]
     * @param keyword 고객사 검색 키워드
     * @param pageable
     * @return Page<Client>
     */
    public Page<Client> searchClients(String keyword, Pageable pageable) {
        if (keyword != null) {
            // 키워드 존재 시 공백 기준으로 잘라서 검색
            String[] words = keyword.split("\\s+");
            String newKeyword = String.join("%", words);
            return clientRepository.findAllByNameContainingOrderBySupportCountDesc(newKeyword, pageable);
        }
        return clientRepository.findAllBySupportCount(pageable);
    }
    /**
     * [ 고객사 상세정보 가져오기 ]
     * @param customerId
     * @return
     */
    public Client getClientDetails(Integer customerId) {
        return clientRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLIENT_NOT_FOUND));
    }

}
