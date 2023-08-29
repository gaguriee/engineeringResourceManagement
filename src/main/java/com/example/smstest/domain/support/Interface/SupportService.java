package com.example.smstest.domain.support.Interface;

import com.example.smstest.domain.support.dto.ModifyRequest;
import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupportService {

    // 필터링 검색
    Page<SupportResponse> searchSupportByFilters(SupportFilterCriteria criteria, Pageable pageable, String sort);

    // 상세보기
    SupportResponse getDetails(Long supportId);

    // 등록
    SupportResponse createSupport(SupportRequest supportRequest);

    // 등록
    SupportResponse modifySupport(ModifyRequest modifyRequest);

    // 삭제
    void deleteSupport(Long supportId);

}