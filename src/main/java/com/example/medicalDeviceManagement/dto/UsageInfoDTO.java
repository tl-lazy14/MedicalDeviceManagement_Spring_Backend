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
public class UsageInfoDTO {
    private String _id;
    private Device device;
    private String usageDepartment;
    private User requester;
    private Date startDate;
    private Date endDate;
}
