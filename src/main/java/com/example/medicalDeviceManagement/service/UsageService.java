package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Using;
import com.example.medicalDeviceManagement.entity.UsingRequest;

import java.text.ParseException;
import java.util.List;

public interface UsageService {
    List<Using> getUsageHistoryOfDevice(String idDevice);
    ListUsageRequestsResponse getUsageRequestByConditions(List<String> selectedUsageDepartment, List<String> selectedStatus, String selectedMonth, String searchQuery, int page, int limit);
    ListUsageInfoResponse getUsageInfoByConditions(List<String> selectedUsageDepartment, String selectedMonth, String searchQuery, int page, int limit);
    MyUsageRequestResponse getMyUsageRequest(String id, List<String> selectedStatus, String searchQuery, int page, int limit);
    List<String> getUsageDepartmentRequestForFilter();
    List<String> getUsageDepartment();
    ExportUsageInfoResponse getAllUsageInfoForExport(List<String> selectedUsageDepartment, String selectedMonth, String searchQuery);
    void addUsageRequest(AddUsageRequest request) throws ParseException;
    void addUsageInfo(AddUsageInfo request) throws ParseException;
    List<UsageInfoDTO> getUsingDevice(String startDate, String endDate) throws ParseException;
    UsingRequest updateApproveStatus(String id, UpdateApproveStatusRequest request);
    UsingRequest editUsageRequest(String id, UpdateUsageRequest request) throws ParseException;
    Using updateUsageInfo(String id, UpdateUsageInfo request) throws ParseException;
    UsingRequest deleteUsageRequest(String id);
    Using deleteUsageInfo(String id);
}
