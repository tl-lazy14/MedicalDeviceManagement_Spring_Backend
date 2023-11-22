package com.example.medicalDeviceManagement.dto;

import com.example.medicalDeviceManagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDTO {
    private String _id;
    private User requester;
    private String deviceName;
    private Integer quantity;
    private Long unitPriceEstimated;
    private Long totalAmountEstimated;
    private Date dateOfRequest;
    private String status;
}
