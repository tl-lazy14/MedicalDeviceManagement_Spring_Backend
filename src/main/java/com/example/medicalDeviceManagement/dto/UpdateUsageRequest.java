package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsageRequest {
    private String usageDepartment;
    private String deviceName;
    private String quantity;
    private String startDate;
    private String endDate;
}
