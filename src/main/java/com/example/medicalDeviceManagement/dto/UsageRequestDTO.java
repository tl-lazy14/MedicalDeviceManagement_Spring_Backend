package com.example.medicalDeviceManagement.dto;

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
public class UsageRequestDTO {
    private String _id;
    private User requester;
    private String usageDepartment;
    private String deviceName;
    private Integer quantity;
    private Date startDate;
    private Date endDate;
    private String status;
}
