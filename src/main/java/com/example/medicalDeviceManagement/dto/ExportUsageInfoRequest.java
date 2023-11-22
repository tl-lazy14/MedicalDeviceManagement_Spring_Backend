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
public class ExportUsageInfoRequest {
    private List<String> selectedUsageDepartment;
    private String selectedMonth;
    private String searchQuery;
}
