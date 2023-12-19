package com.example.smstest.domain.main;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.MempRepository;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
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
            @RequestParam(required = false, defaultValue = "2023") Integer year,
            Model model) {

        Memp memp = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElse(null);
        model.addAttribute("user", memp);

        List<State> states = stateRepository.findAll();
        List<Team> teams_N = teamRepository.findByDepartment_DivisionId(1);
        List<Team> teams_E = teamRepository.findByDepartment_DivisionId(2);

        List<Long> overallSupportTypeHourSums_N = new ArrayList<>();
        Map<String, List<Long>> teamDataMap_N = new HashMap<>();

        List<Long> overallSupportTypeHourSums_E = new ArrayList<>();
        Map<String, List<Long>> teamDataMap_E = new HashMap<>();

        Map<String, String> teamColorMap = new HashMap<>();

        // 전체 데이터 합계 계산
        for (State state : states) {
            Long overallSum_N = supportRepository.findTotalSupportTypeHourByState_N(state, year);
            overallSupportTypeHourSums_N.add(overallSum_N != null ? overallSum_N : 0);

            Long overallSum_E = supportRepository.findTotalSupportTypeHourByState_E(state, year);
            overallSupportTypeHourSums_E.add(overallSum_E != null ? overallSum_E : 0);
        }

        // 팀별 데이터 계산
        for (Team team : teams_N) {
            List<Long> supportTypeHourSums_N = new ArrayList<>();

            for (State state : states) {
                Long sum_N = supportRepository.findTotalSupportTypeHourByStateAndTeam_N( state, team, year );
                supportTypeHourSums_N.add(sum_N != null ? sum_N : 0);
            }
            teamDataMap_N.put(team.getName(), supportTypeHourSums_N);

            teamColorMap.put(team.getName(), team.getColor());
        }

        for (Team team : teams_E) {
            List<Long> supportTypeHourSums_E = new ArrayList<>();

            for (State state : states) {
                Long sum_E = supportRepository.findTotalSupportTypeHourByStateAndTeam_E( state, team, year );
                supportTypeHourSums_E.add(sum_E != null ? sum_E : 0);
            }
            teamDataMap_E.put(team.getName(), supportTypeHourSums_E);

            teamColorMap.put(team.getName(), team.getColor());
        }

        List<Object[]> rankCounts = mempRepository.countByRank();

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : rankCounts) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("rank", row[0]);
            dataPoint.put("count", row[1]);
            chartData.add(dataPoint);
        }

        // 팝업

        List<Announcement> announcements = announcementRepository.findByDisplayTrueOrderByPriorityDesc();


        List<Integer> allSupportYears = supportRepository.findAllYear();
        allSupportYears.sort(Comparator.comparingInt(arr -> (Integer) arr)); // 정수로 캐스팅하여 오름차순 정렬

        model.addAttribute("allSupportYears", allSupportYears);
        model.addAttribute("announcements", announcements);
        model.addAttribute("chartData", chartData);
        model.addAttribute("stateNames", states.stream().map(State::getName).toArray());

        // N본부
        model.addAttribute("overallSupportTypeHourSums_N", overallSupportTypeHourSums_N);
        model.addAttribute("teamDataMap_N", teamDataMap_N);

        // E본부
        model.addAttribute("overallSupportTypeHourSums_E", overallSupportTypeHourSums_E);
        model.addAttribute("teamDataMap_E", teamDataMap_E);

        model.addAttribute("teamColorMap", teamColorMap);

        return "main";
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