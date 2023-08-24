package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
