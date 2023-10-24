package com.example.smstest.domain.organization.dto;

import com.example.smstest.domain.support.dto.SupportResponse;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class MemberInfoDetailDTO {
    private Long memberId;
    private Integer customerId;
    private Long stateId;
    private Page<SupportResponse> posts;
    private int totalPages;
    private int currentPage;
}