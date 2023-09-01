package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByEngineerId(Long engineerId);
    Long countByEngineerId(Long engineerId);
    Long countByEngineerTeamIdAndStateId(Integer engineerId, Long issueId);
    Long countByEngineerTeamIdAndProductId(Integer engineerId, Long issueId);

    Page<Support> findAllByEngineerIdAndCustomerIdAndProductIdAndStateId(Long engineerId, Integer customerId, Long productId, Long stateId, Pageable pageable);
    List<Support> findByEngineerTeamId(Integer teamId);

    @Query("SELECT s.customer.id, s.product.id, s.state.id, SUM(s.supportTypeHour) " +
            "FROM Support s " +
            "WHERE s.engineer.id = :engineerId " +
            "GROUP BY s.customer.id, s.product.id, s.state.id")
    List<Object[]> countAttributesByEngineer(@Param("engineerId") Long engineerId);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state")
    Long findTotalSupportTypeHourByState(@Param("state") State state);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND s.engineer.team = :team")
    Long findTotalSupportTypeHourByStateAndTeam(@Param("state") State state, @Param("team") Team team);

}