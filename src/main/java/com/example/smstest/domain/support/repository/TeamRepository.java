package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Customer;
import com.example.smstest.domain.support.entity.Support;
import com.example.smstest.domain.support.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    List<Team> findByDepartmentId(Integer departmentId);

}
