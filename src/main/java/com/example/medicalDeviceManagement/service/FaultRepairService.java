package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.FaultRepair;

import java.text.ParseException;
import java.util.List;

public interface FaultRepairService {
    List<FaultRepair> getFaultRepairHistoryOfDevice(String idDevice);
    List<Device> getFaultDevice();
    List<FaultRepair> getReparingDevice(String startDate, String endDate) throws ParseException;
    ListFaultRepairResponse getFaultInfoByConditions(List<String> selectedStatus, String searchQuery, int page, int limit);
    ExportFaultRepairResponse getFaultRepairForExport(List<String> selectedStatus, String searchQuery);
    ListFaultRepairResponse getMyFaultReport(String id, List<String> selectedStatus, String searchQuery, int page, int limit);
    void addFaultReport(AddFaultReport report) throws ParseException;
    FaultRepair updateRepairDecision(String id, UpdateRepairDecision repairDecision);
    FaultRepair updateRepairInfo(String id, UpdateRepairInfo request) throws ParseException;
    FaultRepair editFaultReport(String id, UpdateFaultReport report) throws ParseException;
}
