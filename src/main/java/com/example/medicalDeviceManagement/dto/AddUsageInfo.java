package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUsageInfo {
    private List<DeviceUsageInfo> devices;
    private RequesterUsageInfo requester;
    private String usageDepartment;
    private String startDate;
    private String endDate;
}

