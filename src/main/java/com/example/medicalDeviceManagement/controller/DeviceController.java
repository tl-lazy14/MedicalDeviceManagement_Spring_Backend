package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @PostMapping("/get-list-device")
    public ResponseEntity<ListDeviceResponse> getDevicesByConditions(@RequestBody GetListDeviceRequest request) {
        return ResponseEntity.ok().body(deviceService.getDevicesByConditions(request.getSelectedType(), request.getSelectedManufacturer(), request.getSelectedStorageLocation(), request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/export")
    public ResponseEntity<ExportDeviceResponse> getDevicesForExport(@RequestBody ExportDeviceRequest request) {
        return ResponseEntity.ok().body(deviceService.getAllDevicesForExport(request.getSelectedType(), request.getSelectedManufacturer(), request.getSelectedStorageLocation(), request.getSelectedStatus(), request.getSearchQuery()));
    }

    @GetMapping("/manufacturers")
    public ResponseEntity<List<String>> getManufacturerForFilter() {
        List<String> manufacturers = deviceService.getManufacturerForFilter();
        Collections.sort(manufacturers);
        return ResponseEntity.ok().body(manufacturers);
    }

    @GetMapping("/storage-locations")
    public ResponseEntity<List<String>> getStorageForFilter() {
        List<String> storages = deviceService.getStorageForFilter();
        Collections.sort(storages);
        return ResponseEntity.ok().body(storages);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<DeviceDTO> getDeviceByID(@PathVariable(value = "id") String id) {
        DeviceDTO deviceDTO = deviceService.getDeviceByID(id);
        return ResponseEntity.ok().body(deviceDTO);
    }

    @GetMapping("/getName/{id}")
    public ResponseEntity<String> getDeviceNameByID(@PathVariable(value = "id") String deviceID) {
        String deviceName = deviceService.getDeviceNameByID(deviceID);
        return ResponseEntity.ok().body(deviceName);
    }

    @GetMapping("/maintenance/due")
    public ResponseEntity<List<PeriodicMaintenanceInfo>> getDevicesDueForMaintenance() {
        List<PeriodicMaintenanceInfo> periodicMaintenanceInfos = deviceService.getDevicesDueForMaintenance();
        return ResponseEntity.ok().body(periodicMaintenanceInfos);
    }

    @PostMapping
    public ResponseEntity<?> addDevice(@RequestBody DeviceRequest request) {
        try {
            Device newDevice = deviceService.addDevice(request);
            return ResponseEntity.ok().body(newDevice);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> updateDevice(@RequestBody DeviceRequest request, @PathVariable(value = "id") String id) {
        try {
            Device device = deviceService.updateDevice(request, id);
            return ResponseEntity.ok().body(device);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable(value = "id") String id) {
        try {
            Device device = deviceService.deleteDevice(id);
            return ResponseEntity.ok().body(device);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
