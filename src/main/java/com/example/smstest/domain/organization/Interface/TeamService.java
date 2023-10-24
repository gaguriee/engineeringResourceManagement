package com.example.smstest.domain.organization.Interface;

import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    TeamInfoDTO getTeamInfo(Integer teamId);
    MemberInfoDTO getMemberInfo(String name);
    MemberInfoDTO getMemberInfo(Long memberId);
    MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable, String sortOrder);

}
