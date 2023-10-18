package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    List<Team> findByDepartmentId(Integer departmentId);
    Optional<Team> findByName(String teamName);
    List<Team> findByDepartment_DivisionId(Integer divisionId);

}
