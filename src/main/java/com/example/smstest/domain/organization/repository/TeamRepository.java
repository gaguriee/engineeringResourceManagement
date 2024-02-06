package com.example.smstest.domain.organization.repository;

import com.example.smstest.domain.organization.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Team 테이블과 상호작용
 */
public interface TeamRepository extends JpaRepository<Team, Integer> {

    List<Team> findByDepartmentId(Integer departmentId);
    Optional<Team> findFirstByName(String teamName);

}
