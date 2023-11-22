package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceInfoDTO {
    private DeviceUsageInfo device;
    private String startDate;
    private String finishedDate;
    private String performer;
    private String maintenanceServiceProvider;
    private String cost;
}
