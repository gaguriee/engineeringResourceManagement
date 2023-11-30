package com.example.smstest.domain.organization.controller;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.client.repository.ClientRepository;
import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.MemberInfoDetailDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import com.example.smstest.domain.organization.entity.Department;
import com.example.smstest.domain.organization.entity.Division;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.DepartmentRepository;
import com.example.smstest.domain.organization.repository.DivisionRepository;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.organization.service.OraganizationService;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 소속별 조회 관련 Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final MempRepository mempRepository;
    private final OraganizationService oraganizationService;
    private final StateRepository stateRepository;
    private final DivisionRepository divisionRepository;
    private final ClientRepository clientRepository;

    // 날짜 형태 bind
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 소속 빠른 검색
     * @param model
     * @return 본부, 실, 팀, 멤버 전체 데이터 전달
     */
    @GetMapping("/organizationFastSearch")
    public String organizationFastSearch(Model model) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Division> divisions = divisionRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        List<Team> teams = teamRepository.findAll();
        List<Memp> memps = mempRepository.findAll();

        Collections.sort(divisions, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(departments, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(teams, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        Collections.sort(memps, (c1, c2) -> c1.getName().compareTo(c2.getName()));

        model.addAttribute("divisions", divisions);
        model.addAttribute("departments", departments);
        model.addAttribute("teams", teams);
        model.addAttribute("memps", memps);
        model.addAttribute("user", user);

        return "organFastSearch";
    }


    /**
     * 본부별 조회 페이지 (E본부, N본부 등) -> 실 선택 (2실, 4실 등)
     * @param divisionId 본부 Id
     * @param model
     * @return 현재 선택된 본부, 해당 본부에 포함되는 실 데이터 전달
     */
    @GetMapping("/department")
    public String viewDivision(@RequestParam(required = true) Integer divisionId, Model model) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Department> departments = departmentRepository.findByDivisionId(divisionId);
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));

        model.addAttribute("departments", departments);
        model.addAttribute("division", division);

        model.addAttribute("user", user);

        return "organDivision";
    }

    /**
     * 실별 조회 페이지 (2실, 4실 등) -> 팀 선택 (N팀, B팀 등)
     * @param departmentId
     * @param model
     * @return 현재 선택된 실, 해당 실에 포함되는 팀 데이터 전달
     */
    @GetMapping("/team")
    public String viewDepartment(@RequestParam(required = true) Integer departmentId, Model model) {

        List<Team> teams = teamRepository.findByDepartmentId(departmentId);
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));

        model.addAttribute("teams", teams);
        model.addAttribute("department", department);
        model.addAttribute("division", department.getDivision());

        return "organDepartment";
    }

    /**
     * 팀별 조회 페이지 (N팀, B팀 등) -> 엔지니어 선택
     * @param teamId
     * @param model
     * @return 현재 선택된 팀, 해당 팀에 포함되는 엔지니어 데이터 전달
     */
    @GetMapping("/teamInfo")
    public String viewTeam(@RequestParam(required = true) Integer teamId, Model model) {
        TeamInfoDTO teamInfoDTO = oraganizationService.getTeamInfo(teamId);

        if (teamInfoDTO != null) {
            model.addAttribute("memps", teamInfoDTO.getMemps());
            model.addAttribute("team", teamInfoDTO.getTeam());
            model.addAttribute("department", teamInfoDTO.getDepartment());
            model.addAttribute("division", teamInfoDTO.getDepartment().getDivision());

            model.addAttribute("supports", teamInfoDTO.getSupports());
        } else {
            model.addAttribute("errorMessage", "해당 팀을 찾을 수 없습니다.");
        }

        return "organTeam";
    }

    /**
     * 엔지니어 디테일 페이지 (N팀, B팀 등) -> 엔지니어 선택
     * @param memberId
     * @param model
     * @return 현재 선택된 팀, 엔지니어 정보 전달
     */
    @GetMapping("/memberInfo")
    public String getMemberInfo(@RequestParam(required = true) Long memberId, Model model) {
        MemberInfoDTO memberInfoDTO = oraganizationService.getMemberInfo(memberId);

        model.addAttribute("memps", mempRepository.findAllByTeamId(memberInfoDTO.getTeam().getId()));

        model.addAttribute("member", memberInfoDTO.getMemp());
        model.addAttribute("team", memberInfoDTO.getTeam());
        model.addAttribute("department", memberInfoDTO.getDepartment());
        model.addAttribute("division", memberInfoDTO.getDepartment().getDivision());

        model.addAttribute("supports", memberInfoDTO.getSupports());
        model.addAttribute("aggregatedData", memberInfoDTO.getAggregatedData());

        return "organMember";
    }

    /**
     * 엔지니어별 지원내역 조회
     * @param sortOrder 정렬순서
     * @param memberId 엔지니어 id
     * @param customerId 고객사 id
     * @param productId 제품 id
     * @param stateId 상태 id
     * @param pageable 페이지네이션
     * @param model
     * @return
     */
    @GetMapping("/memberInfoDetail")
    public String getMemberInfoDetail(
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = true) Long memberId,
            Integer customerId, Long productId, Long stateId,
            Pageable pageable,
            Model model) {

        MemberInfoDetailDTO memberInfoDetailDTO = oraganizationService.getMemberInfoDetail(memberId, customerId, productId, stateId, pageable, sortOrder);

        model.addAttribute("member", mempRepository.findById(memberInfoDetailDTO.getMemberId()).get());
        model.addAttribute("customer", clientRepository.findById(memberInfoDetailDTO.getCustomerId()).get());
        model.addAttribute("state", stateRepository.findById(memberInfoDetailDTO.getStateId()).get());
        model.addAttribute("posts", memberInfoDetailDTO.getPosts());
        model.addAttribute("totalPages", memberInfoDetailDTO.getTotalPages());
        model.addAttribute("currentPage", memberInfoDetailDTO.getCurrentPage());

        return "organMemberSupportList";
    }


}