package com.example.smstest.domain.organization;

import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.client.ClientRepository;
import com.example.smstest.domain.organization.dto.MemberInfoDTO;
import com.example.smstest.domain.organization.dto.TeamInfoDTO;
import com.example.smstest.domain.organization.entity.Department;
import com.example.smstest.domain.organization.entity.Division;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.DepartmentRepository;
import com.example.smstest.domain.organization.repository.DivisionRepository;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    public String viewTeam(@RequestParam(required = true) Integer teamId,
                           @RequestParam(required = false) java.sql.Date startDate,
                           @RequestParam(required = false) java.sql.Date endDate,
                           Model model) {

        if (startDate==null && endDate==null){
            LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
            startDate = java.sql.Date.valueOf(threeMonthsAgo);
            endDate = java.sql.Date.valueOf(LocalDate.now());
        }

        TeamInfoDTO teamInfoDTO = oraganizationService.getTeamInfo(teamId, startDate, endDate);

        if (teamInfoDTO != null) {
            model.addAttribute("memps", teamInfoDTO.getMemps());
            model.addAttribute("team", teamInfoDTO.getTeam());
            model.addAttribute("department", teamInfoDTO.getDepartment());
            model.addAttribute("division", teamInfoDTO.getDepartment().getDivision());

            model.addAttribute("supports", teamInfoDTO.getSupports());
        } else {
            model.addAttribute("errorMessage", "해당 팀을 찾을 수 없습니다.");
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "organTeam";
    }

    /**
     * 엔지니어 디테일 페이지 (N팀, B팀 등) -> 엔지니어 선택
     * @param memberId
     * @param model
     * @return 현재 선택된 팀, 엔지니어 정보 전달
     */
    @GetMapping("/memberInfo")
    public String getMemberInfo(@RequestParam(required = true) Long memberId,
                                @RequestParam(required = false) Integer clientId,
                                @RequestParam(required = false) java.sql.Date startDate,
                                @RequestParam(required = false) java.sql.Date endDate,
                                Model model) {

        if (startDate == null && endDate == null) {
            LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
            startDate = java.sql.Date.valueOf(threeMonthsAgo);
            endDate = java.sql.Date.valueOf(LocalDate.now());
        }

        MemberInfoDTO memberInfoDTO = oraganizationService.getMemberInfo(memberId, clientId, startDate, endDate);

        model.addAttribute("memps", mempRepository.findAllByTeamIdAndActiveTrue(memberInfoDTO.getTeam().getId()));

        model.addAttribute("member", memberInfoDTO.getMemp());
        model.addAttribute("team", memberInfoDTO.getTeam());
        model.addAttribute("department", memberInfoDTO.getDepartment());
        model.addAttribute("division", memberInfoDTO.getDepartment().getDivision());

        model.addAttribute("supports", memberInfoDTO.getSupports());
        model.addAttribute("allClients", memberInfoDTO.getAllClients());
        model.addAttribute("clients", memberInfoDTO.getClients());

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("clientId", clientId);

        return "organMember";
    }

}