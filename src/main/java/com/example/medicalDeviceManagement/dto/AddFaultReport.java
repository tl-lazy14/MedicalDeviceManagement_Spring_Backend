package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddFaultReport {
    private String reporter;
    private DeviceUsageInfo device;
    private String time;
    private String description;
}
