package com.example.smstest.domain.schedule.service;

import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.schedule.Interface.ScheduleService;
import com.example.smstest.domain.schedule.dto.ScheduleCreateRequest;
import com.example.smstest.domain.schedule.dto.ScheduleEditRequest;
import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.ScheduleRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;

    public List<Schedule> getTodayTeamSchedule(Team team, Date date) {
        // 오늘의 특정 팀 일정을 조회하는 로직을 추가할 수 있음
        return scheduleRepository.findByTeamAndDate(team, date);
    }

    @Override
    public List<Schedule> getThisWeekMemp() {
        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 7);
        Date oneWeekLater = calendar.getTime();
        return scheduleRepository.findByMemp_IdAndDateBetween(memp.getId(), today, oneWeekLater);
    }

    public Schedule saveSchedule(ScheduleCreateRequest scheduleCreateRequest) {
        Schedule schedule = Schedule.builder()
                .content(scheduleCreateRequest.getContent())
                .date(scheduleCreateRequest.getDate())
                .team(teamRepository.findById(scheduleCreateRequest.getTeamId()).get())
                .memp(mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()))
                .build();
        // 스케줄 저장 로직을 추가할 수 있음
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        // 스케줄 삭제 로직을 추가할 수 있음
        scheduleRepository.deleteById(scheduleId);
    }

    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }

    public void updateSchedule(ScheduleEditRequest editRequest, Long scheduleId) {
        // 스케줄을 업데이트하는 로직을 구현합니다.
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null) {
            schedule.setContent(editRequest.getContent());
            schedule.setDate(editRequest.getDate());
            scheduleRepository.save(schedule);
        }
    }
}
