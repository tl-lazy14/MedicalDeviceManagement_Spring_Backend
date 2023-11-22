package com.example.medicalDeviceManagement.controller;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.User;
import com.example.medicalDeviceManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @GetMapping("/getName/{id}")
    public ResponseEntity<String> getUserNameByID(@PathVariable(value = "id") String userID) {
        String userName = userService.getUserNameByID(userID);
        return ResponseEntity.ok().body(userName);
    }

    @GetMapping("/info/department")
    public ResponseEntity<List<String>> getDepartmentsForFilter() {
        return ResponseEntity.ok().body(userService.getDepartmentsForFilter());
    }

    @PostMapping("/info/list")
    public ResponseEntity<ListOperatorResponse> getListOperatorByConditions(@RequestBody GetListOperatorRequest request) {
        ListOperatorResponse listOperatorResponse = userService.getListOperatorByConditions(request.getSelectedDepartment(), request.getSearchQuery(), request.getPage(), request.getLimit());
        return ResponseEntity.ok().body(listOperatorResponse);
    }

    @PostMapping("/info/export")
    public ResponseEntity<ExportOperatorResponse> getListOperatorForExport(@RequestBody ExportOperatorRequest request) {
        ExportOperatorResponse exportOperatorResponse = userService.getListOperatorForExport(request.getSelectedDepartment(), request.getSearchQuery());
        return ResponseEntity.ok().body(exportOperatorResponse);
    }

    @PutMapping("/info/crud/{id}")
    public ResponseEntity<?> updateInfoOperator(@RequestBody UpdateInfoOperatorRequest request, @PathVariable(value = "id") String id) {
        try {
            userService.updateInfoOperator(request, id);
            return ResponseEntity.ok().body("Successfully!");
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") String id) {
        try {
            User operator = userService.deleteUser(id);
            return ResponseEntity.ok().body(operator);
        } catch (Exception e) {
            ErrorResponse errorResponse = ErrorResponse.builder().error(e.getMessage()).build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
