package com.example.medicalDeviceManagement.dto;

import com.example.medicalDeviceManagement.entity.Using;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportUsageInfoResponse {
    private List<Using> list;
}
