package com.example.smstest.domain.auth.repository;

import com.example.smstest.domain.auth.entity.Memp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MempRepository  extends JpaRepository<Memp, Long> {

    List<Memp> findAllByTeamId(Integer teamId);

    Memp findByUsername(String username);

    Memp findOneByName(String mempName);
}
