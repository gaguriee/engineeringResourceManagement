package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    List<Team> findByDepartmentId(Integer departmentId);
    Team findByName(String teamName);

}
