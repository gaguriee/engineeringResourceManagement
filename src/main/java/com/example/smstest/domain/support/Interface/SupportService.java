package com.example.smstest.domain.support.Interface;

import com.example.smstest.domain.support.dto.SupportResponse;

import java.util.List;

public interface SupportService {
    List<SupportResponse> searchSupports(String keyword);
}