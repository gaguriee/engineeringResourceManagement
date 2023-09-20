package com.example.smstest.domain.team.service;

import com.example.smstest.domain.client.repository.CustomerRepository;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.repository.ProductRepository;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.team.Interface.TeamService;
import com.example.smstest.domain.team.dto.AggregatedDataDTO;
import com.example.smstest.domain.team.dto.MemberInfoDTO;
import com.example.smstest.domain.team.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.team.dto.TeamInfoDTO;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.DepartmentRepository;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.ScheduleRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final ProductRepository productRepository;
    private final StateRepository stateRepository;
    private final MempRepository mempRepository;
    private final TeamRepository teamRepository;
    private final SupportRepository supportRepository;
    private final CustomerRepository customerRepository;
    private final DepartmentRepository departmentRepository;
    private ScheduleRepository scheduleRepository;

    public TeamInfoDTO getTeamInfo(Integer teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            List<Memp> memps = mempRepository.findAllByTeamId(teamId);
            List<Support> supports = supportRepository.findByEngineerTeamId(teamId);

            TeamInfoDTO teamInfoDTO = new TeamInfoDTO();
            teamInfoDTO.setMemps(memps);
            teamInfoDTO.setTeam(teamOptional.get());
            teamInfoDTO.setDepartment(teamOptional.get().getDepartment());
            teamInfoDTO.setSupports(supports);

            return teamInfoDTO;
        } else {
            return null;
        }
    }

    public MemberInfoDTO getMemberInfo(Long memberId) {
        Optional<Memp> memp = mempRepository.findById(memberId);
        List<Support> supports = supportRepository.findByEngineerId(memberId);
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
                dto.setCustomerName(customerRepository.findById(dto.getCustomerId()).get().getName());
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

    public MemberInfoDetailDTO getMemberInfoDetail(Long memberId, Integer customerId, Long productId, Long stateId, Pageable pageable, String sortOrder) {
        Page<Support> supports;

        if (sortOrder.equals("asc")){
            supports = supportRepository.findAllByEngineerIdAndCustomerIdAndProductIdAndStateIdOrderBySupportDateAsc(memberId, customerId, productId, stateId, pageable);
        }else{
            supports = supportRepository.findAllByEngineerIdAndCustomerIdAndProductIdAndStateIdOrderBySupportDateDesc(memberId, customerId, productId, stateId, pageable);
        }

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

    public List<Schedule> getTodayTeamSchedule(Team team, Date date) {
        // 오늘의 특정 팀 일정을 조회하는 로직을 추가할 수 있음
        return scheduleRepository.findByTeamAndDate(team, date);
    }

    public Schedule saveSchedule(Schedule schedule) {
        // 스케줄 저장 로직을 추가할 수 있음
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        // 스케줄 삭제 로직을 추가할 수 있음
        scheduleRepository.deleteById(scheduleId);
    }
}
