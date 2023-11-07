package com.example.smstest.license.repository;

import com.example.smstest.license.entity.LicenseProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 라이센스 DB 내 프로젝트 Repository
 */
@Repository
public interface LicenseProjectRepository extends JpaRepository<LicenseProject, String> {
    @Query("SELECT p FROM LicenseProject p WHERE p.projectName LIKE %:keyword% OR p.company.companyName LIKE %:keyword% OR p.projectCode LIKE %:keyword% ORDER BY p.projectRegDate DESC ")
    Page<LicenseProject> findAllByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM LicenseProject p ORDER BY p.projectRegDate DESC ")
    Page<LicenseProject> findAllOrderedByUniqueCodeDesc(Pageable pageable);

}
