package com.example.smstest.domain.support.service;

import com.example.smstest.domain.support.Interface.SupportService;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.mapper.SupportMapper;
import com.example.smstest.domain.support.repository.SupportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportServiceImpl implements SupportService {
    private final SupportRepository supportRepository;

    public SupportServiceImpl(SupportRepository supportRepository) {
        this.supportRepository = supportRepository;
    }

    @Override
    public List<SupportResponse> searchSupports(String keyword) {
        List<Support> supports = supportRepository.findByTitleContainingOrSummaryContainingIgnoreCase(keyword, keyword);

        List<Support> supportList = supports;
        List<SupportResponse> responseList = supportList.stream()
                .map(SupportMapper.INSTANCE::entityToResponse)
                .collect(Collectors.toList());

        return responseList;
    }
}
