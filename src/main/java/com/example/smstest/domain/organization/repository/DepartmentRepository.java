package com.example.smstest.domain.organization.repository;

import com.example.smstest.domain.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByDivisionId(Integer divisionId);

}
