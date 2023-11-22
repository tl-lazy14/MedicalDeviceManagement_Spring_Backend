package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsageInfo {
    private DeviceUsageInfo device;
    private RequesterUsageInfo requester;
    private String usageDepartment;
    private String startDate;
    private String endDate;
}
