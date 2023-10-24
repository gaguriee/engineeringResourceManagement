package com.example.smstest.domain.main.repository;

import com.example.smstest.domain.main.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 팝업(Announcement) 테이블과 상호작용
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByDisplayTrueOrderByPriorityDesc();
}
