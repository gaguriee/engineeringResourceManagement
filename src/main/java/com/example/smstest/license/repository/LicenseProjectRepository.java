package com.example.smstest.license.repository;

import com.example.smstest.license.entity.LicenseProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LicenseProjectRepository extends JpaRepository<LicenseProject, UUID> {

}
