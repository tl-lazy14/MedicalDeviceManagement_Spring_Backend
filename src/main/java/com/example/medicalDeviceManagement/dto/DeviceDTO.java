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
public class DeviceDTO {
    private String _id;
    private String deviceID;
    private String deviceName;
    private String serialNumber;
    private String classification;
    private String manufacturer;
    private String origin;
    private Integer manufacturingYear;
    private Date importationDate;
    private Long price;
    private String storageLocation;
    private Date warrantyPeriod;
    private String maintenanceCycle;
    private String usageStatus;
}
