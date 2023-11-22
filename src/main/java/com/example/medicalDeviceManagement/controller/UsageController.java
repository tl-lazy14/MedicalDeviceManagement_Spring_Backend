package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Using;
import com.example.medicalDeviceManagement.entity.UsingRequest;
import com.example.medicalDeviceManagement.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {
    private final UsageService usageService;

    @GetMapping("/history/{idDevice}")
    public ResponseEntity<List<Using>> getUsageHistoryOfDevice(@PathVariable(value = "idDevice") String idDevice) {
        List<Using> usageHistory = usageService.getUsageHistoryOfDevice(idDevice);
        return ResponseEntity.ok().body(usageHistory);
    }

    @PostMapping("/request/list-request")
    public ResponseEntity<ListUsageRequestsResponse> getUsageRequestByConditions(@RequestBody GetListUsageRequest request) {
        ListUsageRequestsResponse listUsageRequestsResponse = usageService.getUsageRequestByConditions(request.getSelectedUsageDepartment(), request.getSelectedStatus(), request.getSelectedMonth(), request.getSearchQuery(), request.getPage(), request.getLimit());
        return ResponseEntity.ok().body(listUsageRequestsResponse);
    }

    @PostMapping("/info/list-usage")
    public ResponseEntity<ListUsageInfoResponse> getUsageInfoByConditions(@RequestBody GetListUsageInfoRequest request) {
        ListUsageInfoResponse listUsageInfoResponse = usageService.getUsageInfoByConditions(request.getSelectedUsageDepartment(), request.getSelectedMonth(), request.getSearchQuery(), request.getPage(), request.getLimit());
        return ResponseEntity.ok().body(listUsageInfoResponse);
    }

    @PostMapping("/request/operator/{id}")
    public ResponseEntity<MyUsageRequestResponse> getMyUsageRequest(@PathVariable(value = "id") String id,
                                                                    @RequestBody GetMyUsageRequest request) {
        MyUsageRequestResponse myUsageRequestResponse = usageService.getMyUsageRequest(id, request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit());
        return ResponseEntity.ok().body(myUsageRequestResponse);
    }

    @GetMapping("/request/department")
    public ResponseEntity<List<String>> getUsageDepartmentRequestForFilter() {
        return ResponseEntity.ok().body(usageService.getUsageDepartmentRequestForFilter());
    }

    @GetMapping("/info/department")
    public ResponseEntity<List<String>> getUsageDepartment() {
        return ResponseEntity.ok().body(usageService.getUsageDepartment());
    }

    @PostMapping("/export")
    public ResponseEntity<ExportUsageInfoResponse> getAllUsageInfoForExport(@RequestBody ExportUsageInfoRequest request) {
        return ResponseEntity.ok().body(usageService.getAllUsageInfoForExport(request.getSelectedUsageDepartment(), request.getSelectedMonth(), request.getSearchQuery()));
    }

    @PostMapping("/request/create")
    public ResponseEntity<?> addUsageRequest(@RequestBody AddUsageRequest request) throws ParseException {
        usageService.addUsageRequest(request);
        return ResponseEntity.ok().body("Create usage request successfully!");
    }

    @PostMapping("/info")
    public ResponseEntity<?> addUsageInfo(@RequestBody AddUsageInfo request) throws ParseException {
        usageService.addUsageInfo(request);
        return ResponseEntity.ok().body("Add usage info successfully!");
    }

    @GetMapping("/info/using")
    public ResponseEntity<List<UsageInfoDTO>> getUsingDevice(@RequestParam(name = "startDate") String startDate,
                                                      @RequestParam(name = "endDate") String endDate) throws ParseException {
        return ResponseEntity.ok().body(usageService.getUsingDevice(startDate, endDate));
    }

    @PutMapping("/request/status/{id}")
    public ResponseEntity<UsingRequest> updateApproveStatus(@PathVariable(value = "id") String id,
                                                            @RequestBody UpdateApproveStatusRequest request) {
        return ResponseEntity.ok().body(usageService.updateApproveStatus(id, request));
    }

    @PutMapping("/request/crud/{id}")
    public ResponseEntity<UsingRequest> editUsageRequest(@PathVariable(value = "id") String id,
                                                         @RequestBody UpdateUsageRequest request) throws ParseException {
        return ResponseEntity.ok().body(usageService.editUsageRequest(id, request));
    }

    @PutMapping("/info/crud/{id}")
    public ResponseEntity<Using> updateUsageInfo(@PathVariable(value = "id") String id,
                                                 @RequestBody UpdateUsageInfo request) throws ParseException {
        return ResponseEntity.ok().body(usageService.updateUsageInfo(id, request));
    }

    @DeleteMapping("/request/crud/{id}")
    public ResponseEntity<?> deleteUsageRequest(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok().body(usageService.deleteUsageRequest(id));
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error("Đã xảy ra lỗi khi xóa.").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/info/crud/{id}")
    public ResponseEntity<?> deleteUsageInfo(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok().body(usageService.deleteUsageInfo(id));
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error("Đã xảy ra lỗi khi xóa.").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
