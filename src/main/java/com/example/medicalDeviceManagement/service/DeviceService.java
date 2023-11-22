package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;

import java.text.ParseException;
import java.util.List;

public interface DeviceService {
    String getUsageStatus(Device device);
    String calculateUsageStatus(Device device);
    ListDeviceResponse getDevicesByConditions(List<String> selectedType, List<String> selectedManufacturer, List<String> selectedStorageLocation, List<String> selectedStatus, String searchQuery, int page, int limit);
    ExportDeviceResponse getAllDevicesForExport(List<String> selectedType, List<String> selectedManufacturer, List<String> selectedStorageLocation, List<String> selectedStatus, String searchQuery);
    List<String> getManufacturerForFilter();
    List<String> getStorageForFilter();
    DeviceDTO getDeviceByID(String id);
    String getDeviceNameByID(String deviceID);
    List<PeriodicMaintenanceInfo> getDevicesDueForMaintenance();
    Device addDevice(DeviceRequest request) throws ParseException;
    Device updateDevice(DeviceRequest request, String id) throws ParseException;
    Device deleteDevice(String id);

}
