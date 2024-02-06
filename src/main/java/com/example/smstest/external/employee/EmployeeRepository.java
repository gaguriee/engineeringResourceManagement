package com.example.smstest.external.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 인사연동 DB 내 유저 Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Employee findFirstByUserstatusAndUserid(Integer userStatus, String username);

    Optional<Employee> findFirstByUserid(String username);

}
