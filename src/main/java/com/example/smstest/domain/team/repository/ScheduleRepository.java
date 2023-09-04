package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Schedule;
import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDate(Date date);
    List<Schedule> findByTeamAndDate(Team team, Date date);
    List<Schedule> findByMemp_IdAndDateBetween(Long mempId, Date startDate, Date EndDate);

    List<Schedule> findByTeam(Team team);
}