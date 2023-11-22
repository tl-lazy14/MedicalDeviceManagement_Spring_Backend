package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeriodicMaintenanceInfo {
    private String deviceID;
    private String deviceName;
    private boolean dueForMaintenance;
    private Date date;
}
