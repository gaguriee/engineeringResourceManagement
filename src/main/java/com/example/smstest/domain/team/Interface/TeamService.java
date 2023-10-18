package com.example.smstest.domain.team.Interface;

import com.example.smstest.domain.team.dto.MemberInfoDTO;
import com.example.smstest.domain.team.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.team.dto.TeamInfoDTO;
import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

public interface TeamService {
    TeamInfoDTO getTeamInfo(Integer teamId);
    MemberInfoDTO getMemberInfo(String name);
    MemberInfoDTO getMemberInfo(Long memberId);
    MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable, String sortOrder);

}
