package com.example.smstest.domain.project.repository;

import com.example.smstest.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.client.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.uniqueCode) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.projectRegDate DESC")
    Page<Project> findAllByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Project p ORDER BY p.projectRegDate DESC ")
    Page<Project> findAllOrderedByUniqueCodeDesc(Pageable pageable);

    @Query("SELECT p FROM Project p JOIN Support s ON p.id = s.project.id GROUP BY p.id ORDER BY COUNT(s.id) DESC")
    List<Project> findAllByOrderBySupportCountDesc();

    Project findFirstByUniqueCode(String uniqueCode);

    Optional<Project> findFirstByProjectGuid(String projectGuid);

    boolean existsByUniqueCode(String uniqueCode);

}
