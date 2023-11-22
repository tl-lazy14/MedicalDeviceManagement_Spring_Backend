package com.example.medicalDeviceManagement.dto;

import com.example.medicalDeviceManagement.entity.FaultRepair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportFaultRepairResponse {
    private List<FaultRepair> list;
}
