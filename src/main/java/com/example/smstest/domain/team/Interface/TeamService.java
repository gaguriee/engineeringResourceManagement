package com.example.smstest.domain.team.Interface;

import com.example.smstest.domain.team.dto.MemberInfoDTO;
import com.example.smstest.domain.team.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.team.dto.TeamInfoDTO;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    TeamInfoDTO getTeamInfo(Integer teamId);
    MemberInfoDTO getMemberInfo(Long memberId);
    MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable);
}
