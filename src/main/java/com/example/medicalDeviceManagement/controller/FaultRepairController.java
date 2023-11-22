package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.FaultRepair;
import com.example.medicalDeviceManagement.service.FaultRepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/faultRepair")
@RequiredArgsConstructor
public class FaultRepairController {
    private final FaultRepairService faultRepairService;

    @GetMapping("/{idDevice}")
    public ResponseEntity<List<FaultRepair>> getFaultRepairHistoryOfDevice(@PathVariable(value = "idDevice") String idDevice) {
        return ResponseEntity.ok().body(faultRepairService.getFaultRepairHistoryOfDevice(idDevice));
    }

    @GetMapping("/fault/statusFault")
    public ResponseEntity<List<Device>> getFaultDevice() {
        return ResponseEntity.ok().body(faultRepairService.getFaultDevice());
    }

    @GetMapping("/repair/repairing")
    public ResponseEntity<List<FaultRepair>> getReparingDevice(@RequestParam(name = "startDate") String startDate,
                                                               @RequestParam(name = "endDate") String endDate) throws ParseException {
        return ResponseEntity.ok().body(faultRepairService.getReparingDevice(startDate, endDate));
    }

    @PostMapping("/fault/list")
    public ResponseEntity<ListFaultRepairResponse> getFaultInfoByConditions(@RequestBody GetListFaultRepairRequest request) {
        return ResponseEntity.ok().body(faultRepairService.getFaultInfoByConditions(request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/fault-repair/export")
    public ResponseEntity<ExportFaultRepairResponse> getFaultRepairForExport(@RequestBody ExportFaultRepairRequest request) {
        return ResponseEntity.ok().body(faultRepairService.getFaultRepairForExport(request.getSelectedStatus(), request.getSearchQuery()));
    }

    @PostMapping("/fault/operator/{id}")
    public ResponseEntity<ListFaultRepairResponse> getMyFaultReport(@PathVariable(value = "id") String id,
                                                                    @RequestBody GetMyFaultRepairRequest request) {
        return ResponseEntity.ok().body(faultRepairService.getMyFaultReport(id, request.getSelectedStatus(), request.getSearchQuery(), request.getPage(), request.getLimit()));
    }

    @PostMapping("/fault/report")
    public ResponseEntity<String> addFaultReport(@RequestBody AddFaultReport report) throws ParseException {
        faultRepairService.addFaultReport(report);
        return ResponseEntity.ok().body("Create fault report successfully!");
    }

    @PutMapping("/repair/decision/{id}")
    public ResponseEntity<FaultRepair> updateRepairDecision(@PathVariable(value = "id") String id,
                                                            @RequestBody UpdateRepairDecision repairDecision) {
        return ResponseEntity.ok().body(faultRepairService.updateRepairDecision(id, repairDecision));
    }

    @PutMapping("/repair/create-update/{id}")
    public ResponseEntity<FaultRepair> updateRepairInfo(@PathVariable(value = "id") String id,
                                                        @RequestBody UpdateRepairInfo request) throws ParseException {
        return ResponseEntity.ok().body(faultRepairService.updateRepairInfo(id, request));
    }

    @PutMapping("/fault/report/{id}")
    public ResponseEntity<FaultRepair> editFaultReport(@PathVariable(value = "id") String id,
                                                       @RequestBody UpdateFaultReport report) throws ParseException {
        return ResponseEntity.ok().body(faultRepairService.editFaultReport(id, report));
    }
}
