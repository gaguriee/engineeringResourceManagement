package com.example.smstest.scheduler.repository;

import com.example.smstest.scheduler.entity.LicenseProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LicenseProjectRepository extends JpaRepository<LicenseProject, UUID> {

}
