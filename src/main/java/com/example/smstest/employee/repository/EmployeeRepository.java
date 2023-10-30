package com.example.smstest.employee.repository;

import com.example.smstest.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 인사연동 DB 내 유저 Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findByUserid(String username);

}
