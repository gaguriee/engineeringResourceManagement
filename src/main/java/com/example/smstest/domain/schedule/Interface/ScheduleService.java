package com.example.smstest.domain.schedule.Interface;

import com.example.smstest.domain.schedule.dto.ScheduleCreateRequest;
import com.example.smstest.domain.schedule.dto.ScheduleEditRequest;
import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import com.example.smstest.domain.team.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ScheduleService {

    List<Schedule> getTodayTeamSchedule(Team team, Date date);

    List<Schedule> getThisWeekMemp();
    Schedule saveSchedule(ScheduleCreateRequest schedule);

    Schedule getScheduleById(Long scheduleId);

    void updateSchedule(ScheduleEditRequest editRequest, Long scheduleId);

    void deleteSchedule(Long scheduleId);
}
