package com.example.smstest.external.employee;

import com.example.smstest.external.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 인사연동 DB 내 유저 Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findByUserstatusAndUserid(Integer userStatus, String username);

    Optional<Employee> findByUserid(String username);

}
