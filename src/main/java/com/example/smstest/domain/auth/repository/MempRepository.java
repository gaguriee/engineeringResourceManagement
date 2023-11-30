package com.example.smstest.domain.auth.repository;

import com.example.smstest.domain.auth.entity.Memp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Memp Table과 상호작용
 */
public interface MempRepository  extends JpaRepository<Memp, Long> {

    List<Memp> findAllByActiveTrue();

    List<Memp> findAllByTeamId(Integer teamId);

    Optional<Memp> findByUsernameAndActiveTrue(String username);

    Optional<Memp> findOneByName(String mempName);

    @Query("SELECT m.rank, COUNT(m) FROM Memp m GROUP BY m.rank")
    List<Object[]> countByRank();

}
