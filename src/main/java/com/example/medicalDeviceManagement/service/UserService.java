package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.ExportOperatorResponse;
import com.example.medicalDeviceManagement.dto.ListOperatorResponse;
import com.example.medicalDeviceManagement.dto.UpdateInfoOperatorRequest;
import com.example.medicalDeviceManagement.dto.UserDTO;
import com.example.medicalDeviceManagement.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    UserDTO getUserById(String id);
    String getUserNameByID(String userID);
    List<String> getDepartmentsForFilter();
    ListOperatorResponse getListOperatorByConditions(List<String> selectedDepartment, String searchQuery, int page, int limit);
    ExportOperatorResponse getListOperatorForExport(List<String> selectedDepartment, String searchQuery);
    void updateInfoOperator(UpdateInfoOperatorRequest request, String id);
    User deleteUser(String id);
}
