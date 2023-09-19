package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Department;
import com.example.smstest.domain.team.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DivisionRepository extends JpaRepository<Division, Integer> {

}
