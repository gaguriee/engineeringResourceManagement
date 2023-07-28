package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
    // Add custom query methods as needed
}
