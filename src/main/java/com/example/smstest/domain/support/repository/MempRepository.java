package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.Customer;
import com.example.smstest.domain.support.entity.Memp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MempRepository  extends JpaRepository<Memp, Long> {
}
