package com.example.smstest.domain.main.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.main.controller.entity.Announcement;
import com.example.smstest.domain.main.controller.repository.AnnouncementRepository;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("user", memp);

        List<State> states = stateRepository.findAll();
        List<Team> teams = teamRepository.findAll();

        List<Long> overallSupportTypeHourSums = new ArrayList<>();
        Map<String, List<Long>> teamDataMap = new HashMap<>();

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

        return "main";
    }

    @PostMapping("/dismissAnnouncement")
    public ResponseEntity<String> dismissAnnouncement(@RequestParam Integer announcementId) {
        // Get the current user
        Memp currentUser = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        // Find the announcement by ID
        Optional<Announcement> announcementOptional = announcementRepository.findById(announcementId);

        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();

            // Check if the current user's ID is not already in dismissedUsers
            if (!announcement.getDismissedUsers().contains(currentUser.getId())) {
                // Add the current user's ID to dismissedUsers
                announcement.getDismissedUsers().add(currentUser.getId());
                announcementRepository.save(announcement);
            }

            return ResponseEntity.ok("Announcement dismissed.");
        } else {
            return ResponseEntity.badRequest().body("Announcement not found.");
        }
    }

}