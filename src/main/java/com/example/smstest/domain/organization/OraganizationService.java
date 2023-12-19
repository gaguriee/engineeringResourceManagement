package com.example.smstest.domain.organization;

import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.organization.dto.AggregatedDataDTO;
import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 소속별 조회 관련 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OraganizationService {

    private final ProductRepository productRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final SupportRepository supportRepository;
    private final ClientRepository clientRepository;

    /**
     * 팀 정보 조회
     * @param teamId
     * @return
     */
    public TeamInfoDTO getTeamInfo(Integer teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            List<Memp> memps = mempRepository.findAllByTeamId(teamId);

            LocalDateTime fourWeeksAgo = LocalDateTime.now().minus(4, ChronoUnit.WEEKS);
            List<Support> supports = supportRepository.findByTeamIdAndCreatedAtAfter(teamId, fourWeeksAgo);

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
    public MemberInfoDTO getMemberInfo(Long memberId) {
        Optional<Memp> memp = mempRepository.findById(memberId);

        LocalDateTime fourWeeksAgo = LocalDateTime.now().minus(4, ChronoUnit.WEEKS);
        List<Support> supports = supportRepository.findByEngineerIdAndCreatedAtAfter(memberId, fourWeeksAgo);

        Optional<Team> team = teamRepository.findById(memp.get().getTeam().getId());
        List<Memp> memps = mempRepository.findAllByTeamId(team.get().getId());

        List<Object[]> aggregatedData = supportRepository.countAttributesByEngineer(memberId);
        List<AggregatedDataDTO> dtoList = new ArrayList<>();
        for (Object[] data : aggregatedData) {
            AggregatedDataDTO dto = new AggregatedDataDTO();
            dto.setCustomerId((Integer) data[0]);
            dto.setProductId((Long) data[1]);
            dto.setStateId((Long) data[2]);

            if (dto.getCustomerId() != null && dto.getProductId() != null && dto.getStateId() != null) {
                dto.setCustomerName(clientRepository.findById(dto.getCustomerId()).get().getName());
                dto.setProductName(productRepository.findById(dto.getProductId()).get().getName());
                dto.setStateName(stateRepository.findById(dto.getStateId()).get().getName());
                dto.setCount((Double) data[3]);
                dtoList.add(dto);
            }
        }

        MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
        memberInfoDTO.setMemps(memps);
        memberInfoDTO.setDepartment(team.get().getDepartment());
        memberInfoDTO.setMemp(memp.get());
        memberInfoDTO.setTeam(team.get());
        memberInfoDTO.setSupports(supports);
        memberInfoDTO.setAggregatedData(dtoList);

        return memberInfoDTO;
    }

    /**
     * 멤버 지원내역별 조회
     * @param memberId
     * @param customerId
     * @param productId
     * @param stateId
     * @param pageable
     * @param sortOrder
     * @return
     */
    public MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable, String sortOrder) {
        Page<Support> supports;

        // 정렬 방식 선택
        if (sortOrder.equals("asc")){ // 오름차순
            supports = supportRepository.findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateAsc(memberId, customerId, productId, stateId, pageable);
        }else{ // 내림차순
            supports = supportRepository.findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateDesc(memberId, customerId, productId, stateId, pageable);
        }

        // 지원내역 리스트 (Page) 가져오기
        Page<SupportResponse> responsePage = new PageImpl<>(
                supports.getContent().stream()
                        .map(SupportResponse::entityToResponse)
                        .collect(Collectors.toList()),
                supports.getPageable(),
                supports.getTotalElements());

        MemberInfoDetailDTO memberInfoDetailDTO = new MemberInfoDetailDTO();
        memberInfoDetailDTO.setMemberId(memberId);
        memberInfoDetailDTO.setCustomerId(customerId);
        memberInfoDetailDTO.setStateId(stateId);
        memberInfoDetailDTO.setPosts(responsePage);
        memberInfoDetailDTO.setTotalPages(supports.getTotalPages());
        memberInfoDetailDTO.setCurrentPage(pageable.getPageNumber());

        return memberInfoDetailDTO;
    }

}
