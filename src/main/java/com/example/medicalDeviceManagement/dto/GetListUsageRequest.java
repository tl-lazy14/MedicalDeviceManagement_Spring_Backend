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
public class GetListUsageRequest {
    private List<String> selectedUsageDepartment;
    private List<String> selectedStatus;
    private String selectedMonth;
    private String searchQuery;
    private int page;
    private int limit;
}
