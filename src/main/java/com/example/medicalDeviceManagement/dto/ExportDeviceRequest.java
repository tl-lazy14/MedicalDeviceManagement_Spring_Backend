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
public class ExportDeviceRequest {
    private List<String> selectedType;
    private List<String> selectedManufacturer;
    private List<String> selectedStorageLocation;
    private List<String> selectedStatus;
    private String searchQuery;
}
