package com.example.smstest.domain.support.Interface;

import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;

import java.util.List;

public interface SupportService {
    List<SupportResponse> searchSupports(String keyword);

    List<SupportResponse> getFilteredPosts(Long issueId, Long stateId, Long productId, Long customerId);

    SupportResponse createSupport(SupportRequest supportRequest);
}