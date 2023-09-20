package com.example.smstest.domain.main.repository;

import com.example.smstest.domain.main.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByDisplayTrueOrderByPriorityDesc();
}
