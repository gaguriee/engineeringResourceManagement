package com.example.smstest.domain.main.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.main.entity.Announcement;
import com.example.smstest.domain.main.repository.AnnouncementRepository;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MempRepository mempRepository;
    private final SupportRepository supportRepository;
    private final StateRepository stateRepository;
    private final TeamRepository teamRepository;
    private final AnnouncementRepository announcementRepository;

    @GetMapping("/")
    public String main(Model model) {
        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        model.addAttribute("user", memp);

        List<State> states = stateRepository.findAll();
        List<Team> teams = teamRepository.findAll();

        List<Long> overallSupportTypeHourSums = new ArrayList<>();
        Map<String, List<Long>> teamDataMap = new HashMap<>();
        Map<String, String> teamColorMap = new HashMap<>();

        // 전체 데이터 합계 계산
        for (State state : states) {
            Long overallSum = supportRepository.findTotalSupportTypeHourByState(state);
            overallSupportTypeHourSums.add(overallSum != null ? overallSum : 0);
        }

        // 팀별 데이터 계산
        for (Team team : teams) {
            List<Long> supportTypeHourSums = new ArrayList<>();
            for (State state : states) {
                Long sum = supportRepository.findTotalSupportTypeHourByStateAndTeam( state, team );
                supportTypeHourSums.add(sum != null ? sum : 0);
            }
            teamDataMap.put(team.getName(), supportTypeHourSums);
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

        /**
         * 팝업
         */

        List<Announcement> announcements = announcementRepository.findByDisplayTrueOrderByPriorityDesc();

        model.addAttribute("announcements", announcements);
        model.addAttribute("chartData", chartData);
        model.addAttribute("stateNames", states.stream().map(State::getName).toArray());
        model.addAttribute("overallSupportTypeHourSums", overallSupportTypeHourSums);
        model.addAttribute("teamDataMap", teamDataMap);
        model.addAttribute("teamColorMap", teamColorMap);

        return "main";
    }

    @PostMapping("/dismissAnnouncement")
    public ResponseEntity<String> dismissAnnouncement(HttpServletRequest request, @RequestParam Integer announcementId) {

        Memp currentUser = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
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