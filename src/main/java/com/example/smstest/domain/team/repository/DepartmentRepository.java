package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Department;
import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByDivisionId(Integer divisionId);

}
