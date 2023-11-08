package com.example.smstest.domain.organization.dto;

import com.example.smstest.domain.support.dto.SupportResponse;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * Member 별 지원내역 조회 페이지 DTO
 */
@Data
public class MemberInfoDetailDTO {
    private Long memberId;
    private Integer customerId;
    private Long stateId;
    private Page<SupportResponse> posts;
    private int totalPages;
    private int currentPage;
}