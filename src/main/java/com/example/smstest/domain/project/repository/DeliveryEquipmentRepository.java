package com.example.smstest.domain.project.repository;

import com.example.smstest.domain.project.entity.DeliveryEquipment;
import com.example.smstest.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * DeliveryEquipment (납품장비) 테이블과 상호작용
 */
public interface DeliveryEquipmentRepository extends JpaRepository<DeliveryEquipment, Integer> {
    List<DeliveryEquipment> findAllByProjectId(Long projectId);

    @Transactional
    void deleteAllByProject(Project project);
}
