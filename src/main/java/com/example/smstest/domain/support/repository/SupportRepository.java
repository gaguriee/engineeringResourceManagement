package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.*;
import com.example.smstest.domain.organization.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByEngineerId(Long engineerId);

    Page<Support> findAllByProjectIdOrderBySupportDateDesc(Long projectId, Pageable pageable);

    Page<Support> findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateAsc(
            Long engineerId, Integer customerId, Long productId, Long stateId, Pageable pageable);
    Page<Support> findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateDesc(
            Long engineerId, Integer customerId, Long productId, Long stateId, Pageable pageable);

    List<Support> findByEngineerTeamId(Integer teamId);

    @Query("SELECT s.project.client.id, s.product.id, s.state.id, SUM(s.supportTypeHour) " +
            "FROM Support s " +
            "WHERE s.engineer.id = :engineerId " +
            "GROUP BY s.project.client.id, s.product.id, s.state.id")
    List<Object[]> countAttributesByEngineer(@Param("engineerId") Long engineerId);

    // N본부
    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND s.engineer.team.department.division.id = 1")
    Long findTotalSupportTypeHourByState_N(@Param("state") State state);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND s.engineer.team = :team AND s.engineer.team.department.division.id = 1")
    Long findTotalSupportTypeHourByStateAndTeam_N(@Param("state") State state, @Param("team") Team team);

    // E본부
    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND s.engineer.team.department.division.id = 2")
    Long findTotalSupportTypeHourByState_E(@Param("state") State state);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND s.engineer.team = :team AND s.engineer.team.department.division.id = 2")
    Long findTotalSupportTypeHourByStateAndTeam_E(@Param("state") State state, @Param("team") Team team);

    Page<Support> findByTaskTitleContaining(String taskTitle, Pageable pageable);

    List<Support> findTop5ByEngineerIdOrderByCreatedAtDesc(Long userId);

}