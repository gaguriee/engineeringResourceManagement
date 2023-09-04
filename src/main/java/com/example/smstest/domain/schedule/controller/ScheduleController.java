package com.example.smstest.domain.schedule.controller;

import com.example.smstest.domain.schedule.Interface.ScheduleService;
import com.example.smstest.domain.schedule.dto.ScheduleCreateRequest;
import com.example.smstest.domain.schedule.dto.ScheduleEditRequest;
import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final TeamRepository teamRepository;

    @GetMapping("/today")
    public String viewTodaySchedule(Model model, @RequestParam("teamId") Integer teamId) {
        // 오늘의 팀 일정을 조회하고 모델에 추가
        Date today = new Date(); // 오늘 날짜를 얻는 로직
        Optional<Team> team = teamRepository.findById(teamId);

        List<Schedule> teamSchedule = scheduleService.getTodayTeamSchedule(team.get(), today);
        model.addAttribute("teamSchedule", teamSchedule);
        model.addAttribute("team", team.get());
        model.addAttribute("newSchedule", new ScheduleCreateRequest());

        List<Schedule> thisWeekSchedule = scheduleService.getThisWeekMemp();
        model.addAttribute("thisWeekSchedule", thisWeekSchedule);

        return "today_schedule";
    }

    @PostMapping("/add")
    public String addSchedule(@ModelAttribute ScheduleCreateRequest scheduleCreateRequest) {
        System.out.println(scheduleCreateRequest);
        scheduleService.saveSchedule(scheduleCreateRequest);
        return "redirect:/schedule/today?teamId=" + scheduleCreateRequest.getTeamId();
    }

    // 스케줄 수정 페이지로 이동
    @GetMapping("/edit/{scheduleId}")
    public String editSchedule(Model model, @PathVariable Long scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        model.addAttribute("editSchedule", schedule);
        return "edit_schedule";
    }

    // 스케줄 수정
    @PostMapping("/edit/{scheduleId}")
    public String updateSchedule(@ModelAttribute ScheduleEditRequest editRequest, @PathVariable Long scheduleId) {
        scheduleService.updateSchedule(editRequest, scheduleId);
        return "redirect:/schedule/today?teamId=" + editRequest.getTeamId();
    }

    // 스케줄 삭제
    @PostMapping("/delete/{scheduleId}")
    public String deleteSchedule(@PathVariable Long scheduleId, @RequestParam("teamId") Integer teamId) {
        scheduleService.deleteSchedule(scheduleId);
        return "redirect:/schedule/today?teamId=" + teamId;
    }

}
