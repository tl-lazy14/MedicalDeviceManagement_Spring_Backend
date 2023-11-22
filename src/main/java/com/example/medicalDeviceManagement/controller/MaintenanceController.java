package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Maintenance;
import com.example.medicalDeviceManagement.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @GetMapping("/{idDevice}")
    public ResponseEntity<List<Maintenance>> getMaintenanceHistoryOfDevice(@PathVariable(value = "idDevice") String idDevice) {
        return ResponseEntity.ok().body(maintenanceService.getMaintenanceHistoryOfDevice(idDevice));
    }

    @GetMapping("/maintenancing/get-maintenancing-device")
    public ResponseEntity<List<Maintenance>> getMaintenancingDevice(@RequestParam(name = "startDate") String startDate,
                                                                    @RequestParam(name = "endDate") String endDate) throws ParseException {
        return ResponseEntity.ok().body(maintenanceService.getMaintenancingDevice(startDate, endDate));
    }

    @GetMapping("/info/provider")
    public ResponseEntity<List<String>> getServiceProvider() {
        return ResponseEntity.ok().body(maintenanceService.getServiceProvider());
    }

    @PostMapping("/info/list")
    public ResponseEntity<ListMaintenanceResponse> getMaintenanceInfoByConditions(@RequestBody GetListMaintenanceInfoRequest request) {
        return ResponseEntity.ok().body(maintenanceService.getMaintenanceInfoByConditions(request.getSelectedProviders(), request.getSelectedMonth(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/info/export")
    public ResponseEntity<ExportMaintenanceResponse> getAllMaintenanceInfoForExport(@RequestBody ExportMaintenanceInfoRequest request) {
        return ResponseEntity.ok().body(maintenanceService.getAllMaintenanceInfoForExport(request.getSelectedProviders(), request.getSelectedMonth(), request.getSearchQuery()));
    }

    @PostMapping("/info/add")
    public ResponseEntity<String> addMaintenanceInfo(@RequestBody MaintenanceInfoDTO maintenanceInfo) throws ParseException {
        maintenanceService.addMaintenanceInfo(maintenanceInfo);
        return ResponseEntity.ok().body("Add maintenance info successfully!");
    }

    @PutMapping("/info/crud/{id}")
    public ResponseEntity<Maintenance> updateMaintenanceInfo(@PathVariable(value = "id") String id,
                                                             @RequestBody MaintenanceInfoDTO request) throws ParseException {
        return ResponseEntity.ok().body(maintenanceService.updateMaintenanceInfo(id, request));
    }

    @DeleteMapping("/info/crud/{id}")
    public ResponseEntity<?> deleteMaintenanceInfo(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.ok().body(maintenanceService.deleteMaintenanceInfo(id));
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error("Đã xảy ra lỗi khi xóa thông tin bảo trì.").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
