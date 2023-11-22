package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.MaintenanceInfoDTO;
import com.example.medicalDeviceManagement.dto.ExportMaintenanceResponse;
import com.example.medicalDeviceManagement.dto.ListMaintenanceResponse;
import com.example.medicalDeviceManagement.entity.Maintenance;

import java.text.ParseException;
import java.util.List;

public interface MaintenanceService {
    List<Maintenance> getMaintenanceHistoryOfDevice(String idDevice);
    List<Maintenance> getMaintenancingDevice(String startDate, String endDate) throws ParseException;
    List<String> getServiceProvider();
    ListMaintenanceResponse getMaintenanceInfoByConditions(List<String> selectedProviders, String selectedMonth, String searchQuery, int page, int limit);
    ExportMaintenanceResponse getAllMaintenanceInfoForExport(List<String> selectedProviders, String selectedMonth, String searchQuery);
    void addMaintenanceInfo(MaintenanceInfoDTO request) throws ParseException;
    Maintenance updateMaintenanceInfo(String id, MaintenanceInfoDTO request) throws ParseException;
    Maintenance deleteMaintenanceInfo(String id);
}
