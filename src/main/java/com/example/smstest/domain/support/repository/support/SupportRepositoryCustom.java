package com.example.smstest.domain.support.repository.support;

import com.example.smstest.domain.support.dto.SupportFilterCriteria;
import com.example.smstest.domain.support.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL 사용 메서드를 정의하는 인터페이스
 */
public interface SupportRepositoryCustom {
    /**
     * 필터링 인터페이스
     *
     * @param supportFilterCriteria
     * @param pageable
     * @param sort
     * @return
     */
    Page<Support> searchSupportByFilters(SupportFilterCriteria supportFilterCriteria, Pageable pageable, String sort);

}