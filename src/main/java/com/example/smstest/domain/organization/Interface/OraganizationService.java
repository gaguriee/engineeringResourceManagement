package com.example.smstest.domain.organization.Interface;

import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import org.springframework.data.domain.Pageable;

/**
 * 소속별 조회 관련 Service Interface
 */
public interface OraganizationService {

    /**
     * 팀 정보 조회
     * @param teamId
     * @return
     */
    TeamInfoDTO getTeamInfo(Integer teamId);

    /**
     * 멤버 정보 조회
     * @param memberId
     * @return
     */
    MemberInfoDTO getMemberInfo(Long memberId);

    /**
     * 멤버 별 지원내역 조회
     * @param memberId
     * @param customerId
     * @param productId
     * @param stateId
     * @param pageable
     * @param sortOrder
     * @return
     */
    MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable, String sortOrder);

}
