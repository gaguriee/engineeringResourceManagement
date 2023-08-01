package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Customer;
import com.example.smstest.domain.support.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Add custom query methods as needed
}
