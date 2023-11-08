package com.example.smstest.domain.client.repository;

import com.example.smstest.domain.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Client Table과 상호작용
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * 지원내역 수 내림차 순으로 고객사 리스트 (Page)가져오기
     * @param pageable
     * @return 고객사 리스트 (Page)
     */
    @Query("SELECT c FROM Project p JOIN Support s ON p.id = s.project.id JOIN p.client c GROUP BY c.id ORDER BY COUNT(s.id) DESC")
    Page<Client> findAllBySupportCount(Pageable pageable);

    /**
     * 지원내역 수 내림차 순으로 특정 키워드를 포함한 고객사 리스트 (Page) 가져오가
     * @param keyword
     * @param pageable
     * @return 고객사 리스트 (Page)
     */
    @Query("SELECT c FROM Project p JOIN Support s ON p.id = s.project.id JOIN p.client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) GROUP BY c.id ORDER BY COUNT(s.id) DESC")
    Page<Client> findByNameContainingOrderBySupportCountDesc(@Param("keyword") String keyword, Pageable pageable);


    /**
     * 지원내역 수 내림차 순으로 고객사 리스트 가져오기
     * @return 고객사 리스트
     */
    @Query("SELECT c FROM Client c WHERE c IN (SELECT p.client FROM Project p JOIN Support s ON p.id = s.project.id GROUP BY p.client.id ORDER BY COUNT(s.id) DESC)")
    List<Client> findByOrderBySupportCountDesc();

    /**
     * companyGuid로 해당 고객사 존재하는지 찾기
     * @param companyGuid
     * @return 존재 여부
     */
    boolean existsByCompanyGuid(String companyGuid);

    /**
     * companyGuid에 매칭되는 첫번째 요소 가져오기
     * @param companyGuid
     * @return 고객사 객체
     */
    Client findFirstByCompanyGuid(String companyGuid);


}
