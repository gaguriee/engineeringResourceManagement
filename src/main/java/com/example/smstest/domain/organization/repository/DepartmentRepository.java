package com.example.smstest.domain.organization.repository;

import com.example.smstest.domain.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Department(소속) 테이블과 상호작용
 */
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByDivisionId(Integer divisionId);

}
