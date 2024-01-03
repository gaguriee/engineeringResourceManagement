package com.example.smstest.external.license;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 라이센스 DB 내 프로젝트 Repository
 */
@Repository
public interface LicenseProjectRepository extends JpaRepository<LicenseProject, String> {

    Optional<LicenseProject> findFirstByCompany_CompanyGuidAndProjectGuid(String companyGuid, String projectGuid);
    @Query("SELECT p FROM LicenseProject p WHERE " +
            "(:keyword is null OR p.projectName LIKE %:keyword% OR p.company.companyName LIKE %:keyword% OR p.projectCode LIKE %:keyword%) " +
            "ORDER BY p.projectRegDate DESC")
    Page<LicenseProject> findAllByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM LicenseProject p " +
            "ORDER BY " +
            "CASE " +
            "WHEN p.projectGuid = '8032' THEN 1 " +
            "WHEN p.projectGuid = '8031' THEN 2 " +
            "WHEN p.projectGuid = '8030' THEN 3 " +
            "WHEN p.projectGuid = '8029' THEN 4 " +
            "WHEN p.projectGuid = '8028' THEN 5 " +
            "WHEN p.projectGuid = '8027' THEN 6 " +
            "ELSE 6 " +
            "END, " +
            "p.projectRegDate DESC")
    Page<LicenseProject> findAllOrderedByCustomPriority(Pageable pageable);


}
