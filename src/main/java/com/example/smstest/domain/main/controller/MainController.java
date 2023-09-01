package com.example.smstest.domain.main.controller;


import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.repository.StateRepository;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MempRepository mempRepository;
    private final SupportRepository supportRepository;
    private final StateRepository stateRepository;
    private final TeamRepository teamRepository;

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

        model.addAttribute("stateNames", states.stream().map(State::getName).toArray());
        model.addAttribute("overallSupportTypeHourSums", overallSupportTypeHourSums);
        model.addAttribute("teamDataMap", teamDataMap);

        return "main";
    }

}