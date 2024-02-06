package com.example.smstest.domain.support.repository.support;

import com.example.smstest.domain.support.entity.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Support 엔티티에 접근하기 위한 사용자 정의 쿼리 메소드를 정의
 * Spring Data JPA를 구현하기 위한 JpaRepository와 QueryDSL 사용을 위한 SupportRepositoryCustom 확장
 */
public interface SupportRepository extends JpaRepository<Support, Long>, SupportRepositoryCustom {

    // 등록

    /**
     * [ 등록페이지 "최근 등록된 프로젝트" ]
     * 엔지니어 ID를 기반으로 가장 최근에 생성된 Support 항목 5개를 찾습니다.
     *
     * @param userId 엔지니어의 ID.
     * @return 최근에 생성된 Support 엔티티의 목록 (최대 5개).
     */
    List<Support> findTop5ByEngineerIdOrderByCreatedAtDesc(Long userId);

    // 소속별 조회

    /**
     * [ 엔지니어별 디테일페이지 ]
     * 엔지니어 ID와 지원 날짜 범위로 Support 항목을 찾습니다.
     *
     * @param engineerId 엔지니어의 ID.
     * @param startDate  지원 날짜 범위의 시작 날짜.
     * @param endDate    지원 날짜 범위의 끝 날짜.
     * @return Support 엔티티의 목록.
     */
    @Query("SELECT s FROM Support s WHERE s.engineer.id = :engineerId AND s.supportDate BETWEEN :startDate AND :endDate ORDER BY s.supportDate DESC ")
    List<Support> findByEngineerIdAndCreatedAtBetween(
            @Param("engineerId") Long engineerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    /**
     * [ 엔지니어별 조회페이지 "고객사 목록 선택 후 조회" ]
     * 엔지니어 ID, 클라이언트 ID 및 지원 날짜 범위로 Support 항목을 찾습니다.
     *
     * @param engineerId 엔지니어의 ID.
     * @param clientId   클라이언트의 ID.
     * @param startDate  지원 날짜 범위의 시작 날짜.
     * @param endDate    지원 날짜 범위의 끝 날짜.
     * @return Support 엔티티의 목록.
     */
    @Query("SELECT s FROM Support s WHERE s.engineer.id = :engineerId AND s.project.client.id = :clientId AND s.supportDate BETWEEN :startDate AND :endDate ORDER BY s.supportDate DESC ")
    List<Support> findByEngineerIdAndCreatedAtBetween(
            @Param("engineerId") Long engineerId,
            @Param("clientId") Integer clientId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    /**
     * [ 팀별 조회페이지 ]
     * 팀 ID와 지원 날짜 범위로 Support 항목을 찾습니다.
     *
     * @param teamId    팀의 ID.
     * @param startDate 지원 날짜 범위의 시작 날짜.
     * @param endDate   지원 날짜 범위의 끝 날짜.
     * @return Support 엔티티의 목록.
     */
    @Query("SELECT s FROM Support s WHERE s.engineer.team.id = :teamId AND s.supportDate BETWEEN :startDate AND :endDate")
    List<Support> findByTeamIdAndCreatedAtBetween(
            @Param("teamId") Integer teamId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    /**
     * [ 팀별 주간 지원내역 export ]
     *
     * @param teamId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query("SELECT s FROM Support s WHERE s.engineer.team.id = :teamId AND s.supportDate BETWEEN :startDate AND :endDate ORDER BY s.supportDate")
    List<Support> findByTeamIdAndCreatedAtBetweenOrderBySupportDate(
            @Param("teamId") Integer teamId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    // 프로젝트 현황

    /**
     * [ 프로젝트 디테일페이지 ]
     * 프로젝트 ID로 Support 항목을 지원 날짜 내림차순으로 모두 찾습니다.
     *
     * @param projectId 프로젝트의 ID.
     * @param pageable  페이지 설정.
     * @return 페이지별 Support 엔티티의 목록.
     */
    Page<Support> findAllByProjectIdOrderBySupportDateDesc(Long projectId, Pageable pageable);

    // 메인

    /**
     * [ 메인페이지 레이더차트 ]
     * Support 엔티티의 지원 연도 목록을 반환합니다.
     *
     * @return Support 엔티티의 지원 연도 목록.
     */
    @Query("SELECT distinct YEAR(s.supportDate) FROM Support s ")
    List<Integer> findAllYear();

    // N본부

    /**
     * [ 메인페이지 레이더차트 "기술 N본부 업무 집중도 분석" ]
     * N 본부에서 지정한 연도에 대한 각 지원 유형별 지원 시간 합계를 반환합니다.
     *
     * @param year 연도.
     * @return 지원 유형과 해당 연도에 대한 지원 시간 합계를 나타내는 Map 목록.
     */
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

    /**
     * [ 메인페이지 레이더차트 "기술 N본부 팀별 업무 집중도 분석" ]
     * N 본부에서 지정한 연도에 대한 팀별 및 지원 유형별 지원 시간 합계를 반환합니다.
     *
     * @param year 연도.
     * @return 팀명, 지원 유형 및 해당 연도에 대한 지원 시간 합계를 나타내는 Object 배열 목록.
     */
    @Query("SELECT s.engineer.team.name, st.name, SUM(s.supportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 1 " +
            "GROUP BY s.engineer.team.name, st.name")
    List<Object[]> findTotalSupportTypeHourByStateAndTeam_N(@Param("year") Integer year);


    // E 본부

    /**
     * [ 메인페이지 레이더차트 "기술 E본부 업무 집중도 분석" ]
     * E 본부에서 지정한 연도에 대한 각 지원 유형별 지원 시간 합계를 반환합니다.
     *
     * @param year 연도.
     * @return 지원 유형과 해당 연도에 대한 지원 시간 합계를 나타내는 Map 목록.
     */
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

    /**
     * [ 메인페이지 레이더차트 "기술 E본부 팀별 업무 집중도 분석" ]
     * E 본부에서 지정한 연도에 대한 팀별 및 지원 유형별 지원 시간 합계를 반환합니다.
     *
     * @param year 연도.
     * @return 팀명, 지원 유형 및 해당 연도에 대한 지원 시간 합계를 나타내는 Object 배열 목록.
     */
    @Query("SELECT s.engineer.team.name, st.name, SUM(s.supportTypeHour) " +
            "FROM Support s RIGHT JOIN s.state st " +
            "WHERE YEAR(s.supportDate) = :year AND s.engineer.team.department.division.id = 2 " +
            "GROUP BY s.engineer.team.name, st.name")
    List<Object[]> findTotalSupportTypeHourByStateAndTeam_E(@Param("year") Integer year);


}