package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByEngineerId(Long engineerId);
    @Query("SELECT s FROM Support s WHERE s.engineer.id = :engineerId AND s.createdAt >= :fourWeeksAgo")
    List<Support> findByEngineerIdAndCreatedAtAfter(@Param("engineerId") Long engineerId, @Param("fourWeeksAgo") LocalDateTime fourWeeksAgo);
    Page<Support> findAllByProjectIdOrderBySupportDateDesc(Long projectId, Pageable pageable);

    Page<Support> findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateAsc(
            Long engineerId, Integer customerId, Long productId, Long stateId, Pageable pageable);
    Page<Support> findAllByEngineerIdAndProjectClientIdAndProductIdAndStateIdOrderBySupportDateDesc(
            Long engineerId, Integer customerId, Long productId, Long stateId, Pageable pageable);

    List<Support> findByEngineerTeamId(Integer teamId);
    @Query("SELECT s FROM Support s WHERE s.engineer.team.id = :teamId AND s.createdAt >= :fourWeeksAgo")
    List<Support> findByTeamIdAndCreatedAtAfter(@Param("teamId") Integer teamId, @Param("fourWeeksAgo") LocalDateTime fourWeeksAgo);

    @Query("SELECT s.project.client.id, s.product.id, s.state.id, SUM(s.supportTypeHour) " +
            "FROM Support s " +
            "WHERE s.engineer.id = :engineerId " +
            "GROUP BY s.project.client.id, s.product.id, s.state.id")
    List<Object[]> countAttributesByEngineer(@Param("engineerId") Long engineerId);

    // N본부
    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 1")
    Long findTotalSupportTypeHourByState_N(@Param("state") State state, @Param("year") Integer year);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND YEAR(s.supportDate) = :year AND s.engineer.team = :team AND s.engineer.team.department.division.id = 1")
    Long findTotalSupportTypeHourByStateAndTeam_N(@Param("state") State state, @Param("team") Team team, @Param("year") Integer year);

    // E본부
    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 2")
    Long findTotalSupportTypeHourByState_E(@Param("state") State state, @Param("year") Integer year);

    @Query("SELECT SUM(s.supportTypeHour) FROM Support s WHERE s.state = :state AND YEAR(s.supportDate) = :year AND s.engineer.team = :team AND s.engineer.team.department.division.id = 2")
    Long findTotalSupportTypeHourByStateAndTeam_E(@Param("state") State state, @Param("team") Team team, @Param("year") Integer year);

    @Query("SELECT distinct YEAR(s.supportDate) FROM Support s ")
    List<Integer> findAllYear();
    Page<Support> findByTaskTitleContaining(String taskTitle, Pageable pageable);

    List<Support> findTop5ByEngineerIdOrderByCreatedAtDesc(Long userId);

}