package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.PurchaseRequest;
import com.example.medicalDeviceManagement.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-request")
@RequiredArgsConstructor
public class PurchaseRequestController {
    private final PurchaseRequestService purchaseRequestService;

    @PostMapping("/list-request")
    public ResponseEntity<ListPurchaseRequestResponse> getPurchaseRequestByConditions(@RequestBody GetListPurchaseRequest request) {
        return ResponseEntity.ok().body(purchaseRequestService.getPurchaseRequestByConditions(request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/export")
    public ResponseEntity<ExportPurchaseRequestResponse> getPurchaseRequestForExport(@RequestBody ExportPurchaseRequest request) {
        return ResponseEntity.ok().body(purchaseRequestService.getPurchaseRequestForExport(request.getSelectedStatus(), request.getSearchQuery()));
    }

    @PostMapping("/operator/{id}")
    public ResponseEntity<MyPurchaseRequestResponse> getMyPurchaseRequest(@PathVariable(value = "id") String id,
                                                                          @RequestBody GetMyPurchaseRequest request) {
        return ResponseEntity.ok().body(purchaseRequestService.getMyPurchaseRequest(id, request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/create")
    public ResponseEntity<String> addPurchaseRequest(@RequestBody AddPurchaseRequest purchaseRequest) {
        purchaseRequestService.addPurchaseRequest(purchaseRequest);
        return ResponseEntity.ok().body("Create purchase request successfully!");
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<PurchaseRequest> updateApproveStatus(@PathVariable(value = "id") String id,
                                                               @RequestBody UpdateApproveStatusRequest statusRequest) {
        return ResponseEntity.ok().body(purchaseRequestService.updateApproveStatus(id, statusRequest));
    }

    @PutMapping("/crud/{id}")
    public ResponseEntity<PurchaseRequest> editPurchaseRequest(@PathVariable(value = "id") String id,
                                                               @RequestBody UpdatePurchaseRequest request) {
        return ResponseEntity.ok().body(purchaseRequestService.editPurchaseRequest(id, request));
    }

    @DeleteMapping("/crud/{id}")
    public ResponseEntity<?> deletePurchaseRequest(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok().body(purchaseRequestService.deletePurchaseRequest(id));
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error("Đã xảy ra lỗi khi xóa.").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
