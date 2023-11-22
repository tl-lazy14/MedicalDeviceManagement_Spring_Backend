package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.PurchaseRequest;

import java.util.List;

public interface PurchaseRequestService {
    ListPurchaseRequestResponse getPurchaseRequestByConditions(List<String> selectedStatus, String searchQuery, int page, int limit);
    ExportPurchaseRequestResponse getPurchaseRequestForExport(List<String> selectedStatus, String searchQuery);
    MyPurchaseRequestResponse getMyPurchaseRequest(String id, List<String> selectedStatus, String searchQuery, int page, int limit);
    void addPurchaseRequest(AddPurchaseRequest purchaseRequest);
    PurchaseRequest updateApproveStatus(String id, UpdateApproveStatusRequest status);
    PurchaseRequest editPurchaseRequest(String id, UpdatePurchaseRequest request);
    PurchaseRequest deletePurchaseRequest(String id);
}
