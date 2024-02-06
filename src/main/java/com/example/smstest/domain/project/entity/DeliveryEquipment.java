package com.example.smstest.domain.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * 프로젝트 내 납품장비 Entity
 */
@Entity
@Data
@ToString(exclude = "project")
@Table(name = "delivery_equipment")
public class DeliveryEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "납품장비_id")
    private Integer deliveryEquipmentId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "프로젝트_id")
    private Project project;

    @Column(name = "수량")
    private Integer quantity;

    @Column(name = "납품장비명", length = 255)
    private String equipmentName;

    public static DeliveryEquipment of(DeliveryEquipment request) {
        DeliveryEquipment equipment = new DeliveryEquipment();
        equipment.setProject(request.getProject());
        equipment.setQuantity(request.getQuantity());
        equipment.setEquipmentName(request.getEquipmentName());
        return equipment;
    }

}
