package com.example.smstest.domain.support.dto;

import com.example.smstest.domain.support.repository.CustomerRepository;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AggregatedDataDTO {
    private String customerName;
    private Long customerId;
    private String productName;
    private Long productId;
    private String stateName;
    private Long stateId;
    private Long count;

    @Builder
    public AggregatedDataDTO(String customerName, Long customerId, String productName, Long productId, String stateName, Long stateId, Long count) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.productName = productName;
        this.productId = productId;
        this.stateName = stateName;
        this.stateId = stateId;
        this.count = count;
    }


}