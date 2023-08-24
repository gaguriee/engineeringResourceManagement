package com.example.smstest.domain.team.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregatedDataDTO {
    private String customerName;
    private Integer customerId;
    private String productName;
    private Long productId;
    private String stateName;
    private Long stateId;
    private Long count;

    @Builder
    public AggregatedDataDTO(String customerName, Integer customerId, String productName, Long productId, String stateName, Long stateId, Long count) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.productName = productName;
        this.productId = productId;
        this.stateName = stateName;
        this.stateId = stateId;
        this.count = count;
    }


}