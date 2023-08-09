package com.example.smstest.domain.support.Interface;

import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupportService {

    // 키워드 검색
//    List<SupportResponse> searchSupports(String keyword);

    // 필터링
//    List<SupportResponse> getFilteredPosts(Long issueId, Long stateId, Long productId, Long customerId);
    Page<Support> searchSupportByFilters(SupportFilterCriteria criteria, Pageable pageable, String sort);


    // 상세보기
    SupportResponse getDetails(Long supportId);


    // 등록
    SupportResponse createSupport(SupportRequest supportRequest);
}