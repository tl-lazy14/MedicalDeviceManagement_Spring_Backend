package com.example.medicalDeviceManagement.dto;

import com.example.medicalDeviceManagement.entity.Device;
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
public class FaultRepairDTO {
    private String _id;
    private Device device;
    private User reporter;
    private Date time;
    private String description;
    private String repairStatus;
    private Date startDate;
    private Date finishedDate;
    private String repairServiceProvider;
    private Long cost;
}
