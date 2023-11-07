package com.example.smstest.domain.project.repository;

import com.example.smstest.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.name LIKE %:keyword% OR p.client.name LIKE %:keyword% OR p.uniqueCode LIKE %:keyword% ORDER BY p.projectRegDate DESC ")
    Page<Project> findAllByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Project p ORDER BY p.projectRegDate DESC ")
    Page<Project> findAllOrderedByUniqueCodeDesc(Pageable pageable);

    @Query("SELECT p FROM Project p JOIN Support s ON p.id = s.project.id GROUP BY p.id ORDER BY COUNT(s.id) DESC")
    List<Project> findAllByOrderBySupportCountDesc();

    Project findFirstByUniqueCode(String uniqueCode);

    Optional<Project> findFirstByProjectGuid(UUID projectGuid);

    boolean existsByUniqueCode(String uniqueCode);

}
