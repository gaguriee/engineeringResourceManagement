package com.example.smstest.license.repository;

import com.example.smstest.license.entity.LicenseProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 라이센스 DB 내 프로젝트 Repository
 */
@Repository
public interface LicenseProjectRepository extends JpaRepository<LicenseProject, UUID> {

}
