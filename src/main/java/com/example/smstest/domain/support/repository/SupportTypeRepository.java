package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.SupportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTypeRepository extends JpaRepository<SupportType, Long> {
}
