package com.example.medicalDeviceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequest {
    private String deviceID;
    private String deviceName;
    private String serialNumber;
    private String classification;
    private String manufacturer;
    private String origin;
    private String manufacturingYear;
    private String importationDate;
    private String price;
    private String storageLocation;
    private String warrantyPeriod;
    private String maintenanceCycle;
}
