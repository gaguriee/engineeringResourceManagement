package com.example.smstest.domain.organization.repository;

import com.example.smstest.domain.organization.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Division(본부) 테이블과 상호작용
 */
public interface DivisionRepository extends JpaRepository<Division, Integer> {

}
