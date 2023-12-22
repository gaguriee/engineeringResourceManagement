package com.example.smstest.domain.support.repository;

import com.example.smstest.domain.support.entity.State;
import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom  {
    List<Support> findByEngineerId(Long engineerId);
    @Query("SELECT s FROM Support s WHERE s.engineer.id = :engineerId AND s.supportDate BETWEEN :startDate AND :endDate ORDER BY s.supportDate DESC ")
    List<Support> findByEngineerIdAndCreatedAtBetween(
            @Param("engineerId") Long engineerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT s FROM Support s WHERE s.engineer.id = :engineerId AND s.project.client.id = :clientId AND s.supportDate BETWEEN :startDate AND :endDate ORDER BY s.supportDate DESC ")
    List<Support> findByEngineerIdAndCreatedAtBetween(
            @Param("engineerId") Long engineerId,
            @Param("clientId") Integer clientId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    @Query("SELECT s FROM Support s WHERE s.engineer.team.id = :teamId AND s.supportDate BETWEEN :startDate AND :endDate")
    List<Support> findByTeamIdAndCreatedAtBetween(
            @Param("teamId") Integer teamId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

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
    @Query("SELECT SUM(s.supportTypeHour) FROM Support s " +
            "JOIN s.engineer e " +
            "WHERE s.state = :state " +
            "AND YEAR(s.supportDate) = :year " +
            "AND e.team.department.division.id = 1")
    Long findTotalSupportTypeHourByState_N(@Param("state") State state,
                                           @Param("year") Integer year);

    @Query("SELECT NEW map(st.name AS state, SUM(s.supportTypeHour) AS totalSupportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 1 " +
            "GROUP BY st.name " +
            "ORDER BY CASE st.name " +
            "   WHEN '납품' THEN 1 " +
            "   WHEN 'WA' THEN 2 " +
            "   WHEN 'MA' THEN 3 " +
            "   WHEN 'Pre-Sales' THEN 4 " +
            "   WHEN '협업' THEN 5 " +
            "   WHEN '업무혁신' THEN 6 " +
            "   ELSE 7 END"
    )
    List<Map<String, Long>> findTotalSupportTypeHourByState_N(@Param("year") Integer year);

    @Query("SELECT NEW map(st.name AS state, SUM(s.supportTypeHour) AS totalSupportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 2 " +
            "GROUP BY st.name " +
            "ORDER BY CASE st.name " +
            "   WHEN '납품' THEN 1 " +
            "   WHEN 'WA' THEN 2 " +
            "   WHEN 'MA' THEN 3 " +
            "   WHEN 'Pre-Sales' THEN 4 " +
            "   WHEN '협업' THEN 5 " +
            "   WHEN '업무혁신' THEN 6 " +
            "   ELSE 7 END"
    )
    List<Map<String, Long>> findTotalSupportTypeHourByState_E(@Param("year") Integer year);


    @Query("SELECT s.engineer.team.name, st.name, SUM(s.supportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 1 " +
            "GROUP BY s.engineer.team.name, st.name")
    List<Object[]> findTotalSupportTypeHourByStateAndTeam_N(@Param("year") Integer year);

    @Query("SELECT s.engineer.team.name, st.name, SUM(s.supportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 2 " +
            "GROUP BY s.engineer.team.name, st.name")
    List<Object[]> findTotalSupportTypeHourByStateAndTeam_E(@Param("year") Integer year);

    @Query("SELECT distinct YEAR(s.supportDate) FROM Support s ")
    List<Integer> findAllYear();

    List<Support> findTop5ByEngineerIdOrderByCreatedAtDesc(Long userId);

}