package com.example.smstest.domain.team.controller;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.customer.repository.CustomerRepository;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.team.Interface.TeamService;
import com.example.smstest.domain.team.dto.MemberInfoDTO;
import com.example.smstest.domain.team.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.team.dto.TeamInfoDTO;
import com.example.smstest.domain.team.entity.Department;
import com.example.smstest.domain.team.entity.Division;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.DepartmentRepository;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.DivisionRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TeamController {
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final MempRepository mempRepository;
    private final TeamService teamService;
    private final CustomerRepository customerRepository;
    private final StateRepository stateRepository;
    private final DivisionRepository divisionRepository;

    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

//
//    /**
//     * 본부 (기술 N본부, E본부 등)
//     */
//    @GetMapping("/division")
//    public String selectDivision(Model model) {
//
//        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//        List<Division> divisions = divisionRepository.findAll();
//
//        model.addAttribute("divisions", divisions);
//        model.addAttribute("user", user);
//
//        return "division";
//    }

    /**
     * 소속 (2실, 4실 등)
     */
    @GetMapping("/department")
    public String selectDepartment(Model model) {

        Memp user = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("departments", departments);
        model.addAttribute("user", user);

        return "department";
    }

    /**
     * 팀 (N팀, B팀 등)
     */
    @GetMapping("/team")
    public String selectTeam(@RequestParam(required = true) Integer departmentId,Model model) {

        List<Team> teams = teamRepository.findByDepartmentId(departmentId);
        Optional<Department> department = departmentRepository.findById(departmentId);

        model.addAttribute("teams", teams);
        model.addAttribute("department", department.get());

        return "team";
    }

    @GetMapping("/teamInfo")
    public String getTeamInfo(@RequestParam(required = true) Integer teamId, Model model) {
        TeamInfoDTO teamInfoDTO = teamService.getTeamInfo(teamId);

        if (teamInfoDTO != null) {
            model.addAttribute("memps", teamInfoDTO.getMemps());
            model.addAttribute("team", teamInfoDTO.getTeam());
            model.addAttribute("department", teamInfoDTO.getDepartment());
            model.addAttribute("supports", teamInfoDTO.getSupports());
        } else {
            model.addAttribute("errorMessage", "해당 팀을 찾을 수 없습니다.");
        }

        return "teamInfo";
    }

    // 팀원 정보 조회
    @GetMapping("/memberInfo")
    public String getMemberInfo(@RequestParam(required = true) Long memberId, Model model) {
        MemberInfoDTO memberInfoDTO = teamService.getMemberInfo(memberId);

        model.addAttribute("memps", mempRepository.findAllByTeamId(memberInfoDTO.getTeam().getId()));

        model.addAttribute("department", memberInfoDTO.getDepartment());
        model.addAttribute("member", memberInfoDTO.getMemp());
        model.addAttribute("team", memberInfoDTO.getTeam());
        model.addAttribute("supports", memberInfoDTO.getSupports());
        model.addAttribute("aggregatedData", memberInfoDTO.getAggregatedData());

        return "memberInfo";
    }

    // 팀원 정보 조회 - 지원내역 리스트
    @GetMapping("/memberInfoDetail")
    public String getMemberInfoDetail(
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = true) Long memberId,
            Integer customerId, Long productId, Long stateId,
            Pageable pageable,
            Model model) {

        MemberInfoDetailDTO memberInfoDetailDTO = teamService.getMemberInfoDetail(memberId, customerId, productId, stateId, pageable, sortOrder);

        model.addAttribute("member", mempRepository.findById(memberInfoDetailDTO.getMemberId()).get());
        model.addAttribute("customer", customerRepository.findById(memberInfoDetailDTO.getCustomerId()).get());
        model.addAttribute("state", stateRepository.findById(memberInfoDetailDTO.getStateId()).get());
        model.addAttribute("posts", memberInfoDetailDTO.getPosts());
        model.addAttribute("totalPages", memberInfoDetailDTO.getTotalPages());
        model.addAttribute("currentPage", memberInfoDetailDTO.getCurrentPage());

        return "memberInfoDetail";
    }


}