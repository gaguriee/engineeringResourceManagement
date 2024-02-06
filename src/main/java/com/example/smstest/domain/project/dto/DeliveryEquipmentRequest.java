package com.example.smstest.domain.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 납품장비 Request DTO
 */
@Data
@NoArgsConstructor
public class DeliveryEquipmentRequest {

    private Long projectId;

    private Integer quantity;

    private String equipmentName;

}