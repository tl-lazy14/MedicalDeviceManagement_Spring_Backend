package com.example.medicalDeviceManagement.dto;

import com.example.medicalDeviceManagement.entity.Device;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceDTO {
    private String _id;
    private Device device;
    private Date startDate;
    private Date finishedDate;
    private String performer;
    private Long cost;
    private String maintenanceServiceProvider;
}
