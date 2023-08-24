package com.example.smstest.domain.team.repository;

import com.example.smstest.domain.team.entity.Memp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MempRepository  extends JpaRepository<Memp, Long> {

    @Query("SELECT m.rank, COUNT(m) FROM Memp m GROUP BY m.rank")
    List<Object[]> countEmployeesByJobPosition();

    List<Memp> findAllByTeamId(Integer teamId);


    Memp findOneByName(String mempName);
}
