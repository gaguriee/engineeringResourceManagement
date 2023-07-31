package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportRepositoryCustom {
    Page<Support> searchSupportByFilters(SupportFilterCriteria supportFilterCriteria, Pageable pageable);

}