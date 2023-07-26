package com.example.smstest.support.repository;

import com.example.smstest.support.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SupportRepository extends JpaRepository<Support, String> {

}