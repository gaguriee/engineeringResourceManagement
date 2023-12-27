package com.example.smstest.domain.organization;

import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.Client;
import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.project.Project;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.support.SupportRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 소속별 조회 관련 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OraganizationService {

    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final SupportRepository supportRepository;

    /**
     * 팀 정보 조회
     * @param teamId
     * @return
     */
    public TeamInfoDTO getTeamInfo(Integer teamId, Date startDate, Date endDate) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            List<Memp> memps = mempRepository.findAllByTeamIdAndActiveTrue(teamId);

            List<Support> supports = supportRepository.findByTeamIdAndCreatedAtBetween(teamId, startDate, endDate);

            TeamInfoDTO teamInfoDTO = new TeamInfoDTO();
            teamInfoDTO.setMemps(memps); // 해당 팀 소속 엔지니어
            teamInfoDTO.setTeam(teamOptional.get()); // 팀
            teamInfoDTO.setDepartment(teamOptional.get().getDepartment()); // 소속 (실)
            teamInfoDTO.setSupports(supports); // 지원내역

            return teamInfoDTO;
        } else {
            throw new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
    }

    /**
     * 멤버 정보 조회
     * @param memberId
     * @return
     */
    public MemberInfoDTO getMemberInfo(Long memberId, Integer clientId, Date startDate, Date endDate) {
        Optional<Memp> memp = mempRepository.findById(memberId);

        List<Support> allSupports = supportRepository.findByEngineerIdAndCreatedAtBetween(memberId, startDate, endDate);

        List<Support> supports = null;

        if (clientId==null){
            supports = allSupports;
        }
        else {
            supports = supportRepository.findByEngineerIdAndCreatedAtBetween(memberId, clientId, startDate, endDate);
        }
        List<Client> clients = supports.stream()
                .map(Support::getProject)
                .filter(Objects::nonNull) // Project가 null이 아닌 경우에만 진행
                .map(Project::getClient)
                .filter(Objects::nonNull) // Client가 null이 아닌 경우에만 진행
                .distinct()
                .collect(Collectors.toList());

        List<Client> allClients = allSupports.stream()
                .map(Support::getProject)
                .filter(Objects::nonNull) // Project가 null이 아닌 경우에만 진행
                .map(Project::getClient)
                .filter(Objects::nonNull) // Client가 null이 아닌 경우에만 진행
                .distinct()
                .collect(Collectors.toList());

        Optional<Team> team = teamRepository.findById(memp.get().getTeam().getId());
        List<Memp> memps = mempRepository.findAllByTeamIdAndActiveTrue(team.get().getId());

        MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
        memberInfoDTO.setMemps(memps);
        memberInfoDTO.setDepartment(team.get().getDepartment());
        memberInfoDTO.setMemp(memp.get());
        memberInfoDTO.setTeam(team.get());
        memberInfoDTO.setSupports(supports);
        memberInfoDTO.setClients(clients);
        memberInfoDTO.setAllClients(allClients);

        return memberInfoDTO;
    }

}
