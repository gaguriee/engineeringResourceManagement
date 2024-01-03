package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.entity.Memp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Memp Table과 상호작용
 */
public interface MempRepository  extends JpaRepository<Memp, Long> {

    /**
     * active가 true인 모든 memp 검색
     * @return
     */
    List<Memp> findAllByActiveTrue();

    /**
     * active가 true인 모든 memp를 팀별로 검색
     * @param teamId
     * @return
     */
    List<Memp> findAllByTeamIdAndActiveTrue(Integer teamId);

    /**
     * active가 true인 유저를 username으로 검색 (첫번째 결과 반환)
     * @param username
     * @return
     */
    Optional<Memp> findFirstByUsernameAndActiveTrue(String username);

}
