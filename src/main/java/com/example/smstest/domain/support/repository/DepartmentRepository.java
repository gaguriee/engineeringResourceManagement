package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Department;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
