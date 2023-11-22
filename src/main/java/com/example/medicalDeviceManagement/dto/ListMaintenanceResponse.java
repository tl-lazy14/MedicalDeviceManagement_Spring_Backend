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
public class ListMaintenanceResponse {
    private List<MaintenanceDTO> list;
    private Long totalRecords;
    private int totalPages;
}