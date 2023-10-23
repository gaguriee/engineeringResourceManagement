package com.example.smstest.domain.client.repository;

import com.example.smstest.domain.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query("SELECT c FROM Project p JOIN Support s ON p.id = s.project.id JOIN p.client c GROUP BY c.id ORDER BY COUNT(s.id) DESC")
    Page<Client> findAllBySupportCount(Pageable pageable);

    @Query("SELECT c FROM Project p JOIN Support s ON p.id = s.project.id JOIN p.client c WHERE c.name LIKE %:keyword% GROUP BY c.id ORDER BY COUNT(s.id) DESC")
    Page<Client> findByNameContainingOrderBySupportCountDesc(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT c FROM Client c WHERE c IN (SELECT p.client FROM Project p JOIN Support s ON p.id = s.project.id GROUP BY p.client.id ORDER BY COUNT(s.id) DESC)")
    List<Client> findByOrderBySupportCountDesc();

    Client findOneByName(String customerName);
    boolean existsByName(String name);
    boolean existsByCompanyGuid(String companyGuid);
    Client findFirstByCompanyGuid(String companyGuid);


}
