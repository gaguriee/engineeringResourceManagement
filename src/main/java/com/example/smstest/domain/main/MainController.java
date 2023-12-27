package com.example.smstest.domain.main;


import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.support.SupportRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

/**
 * 메인 페이지 관련 Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MempRepository mempRepository;
    private final SupportRepository supportRepository;
    private final StateRepository stateRepository;
    private final TeamRepository teamRepository;
    private final AnnouncementRepository announcementRepository;

    /**
     * 메인 페이지
     * @param model
     * @return 레더 차트를 위한 전체/팀별 데이터, 팝업 데이터 전달
     */
    @GetMapping("/")
    public String main(
            @RequestParam(required = false) Integer radarchartYear,
            Model model) {

        if (radarchartYear == null) {
            radarchartYear = LocalDate.now().getYear();
        }

        Memp memp = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        model.addAttribute("user", memp);

        List<State> states = stateRepository.findAll();

        Map<String, String> teamColorMap = new HashMap<>();

        // 팀별 데이터 계산
        for (Team team : teamRepository.findAll()) {
            teamColorMap.put(team.getName(), team.getColor());
        }

        List<Map<String, Long>> resultInN = supportRepository.findTotalSupportTypeHourByState_N(radarchartYear);
        List<Object[]> resultByTeamInN = supportRepository.findTotalSupportTypeHourByStateAndTeam_N(radarchartYear);
        Map<String, Map<String, Double>> resultMapByTeamInN = new HashMap<>();

        List<Map<String, Long>> resultInE = supportRepository.findTotalSupportTypeHourByState_E(radarchartYear);
        List<Object[]> resultByTeamInE = supportRepository.findTotalSupportTypeHourByStateAndTeam_E(radarchartYear);
        Map<String, Map<String, Double>> resultMapByTeamInE = new HashMap<>();

        for (Object[] row : resultByTeamInN) {
            String teamName = (String) row[0];
            String stateName = (String) row[1];
            Double totalHour = (Double) row[2];

            resultMapByTeamInN.computeIfAbsent(teamName, k -> new TreeMap<>(Comparator.comparingInt(this::getStateOrder))).put(stateName, totalHour);
        }

        for (Object[] row : resultByTeamInE) {
            String teamName = (String) row[0];
            String stateName = (String) row[1];
            Double totalHour = (Double) row[2];

            resultMapByTeamInE.computeIfAbsent(teamName, k -> new TreeMap<>(Comparator.comparingInt(this::getStateOrder))).put(stateName, totalHour);
        }

        // 팝업

        List<Announcement> announcements = announcementRepository.findByDisplayTrueOrderByPriorityDesc();

        List<Integer> allSupportYears = supportRepository.findAllYear();
        allSupportYears.sort(Comparator.comparingInt(arr -> (Integer) arr)); // 정수로 캐스팅하여 오름차순 정렬

        model.addAttribute("allSupportYears", allSupportYears);
        model.addAttribute("announcements", announcements);
        model.addAttribute("stateNames", states.stream().map(State::getName).toArray());

        // N본부
        model.addAttribute("resultInN", resultInN);
        model.addAttribute("resultMapByTeamInN", resultMapByTeamInN);

        // E본부
        model.addAttribute("resultInE", resultInE);
        model.addAttribute("resultMapByTeamInE", resultMapByTeamInE);

        model.addAttribute("teamColorMap", teamColorMap);
        model.addAttribute("radarchartYear", radarchartYear);

        return "main";
    }

    private int getStateOrder(String stateName) {
        switch (stateName) {
            case "납품":
                return 1;
            case "WA":
                return 2;
            case "MA":
                return 3;
            case "Pre-Sales":
                return 4;
            case "협업":
                return 5;
            case "혁신성과":
                return 6;
            default:
                return Integer.MAX_VALUE; // 기타 상태에 대한 처리
        }
    }

    /**
     * 메인 팝업창 그만보기 옵션
     * @param request
     * @param announcementId 팝업 Id
     * @return 실행 결과 전달
     */
    @PostMapping("/dismissAnnouncement")
    public ResponseEntity<String> dismissAnnouncement(HttpServletRequest request, @RequestParam Integer announcementId) {

        Memp currentUser = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Announcement> announcementOptional = announcementRepository.findById(announcementId);

        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();

            if (!announcement.getDismissedUsers().contains(currentUser.getId())) {
                announcement.getDismissedUsers().add(currentUser.getId());
                announcementRepository.save(announcement);
            }

            return ResponseEntity.ok("Announcement dismissed.");
        } else {
            return ResponseEntity.badRequest().body("Announcement not found.");
        }
    }

}