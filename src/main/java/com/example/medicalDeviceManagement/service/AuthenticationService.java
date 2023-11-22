package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.AuthenticationResponse;
import com.example.medicalDeviceManagement.dto.ChangePasswordRequest;
import com.example.medicalDeviceManagement.dto.LoginRequest;
import com.example.medicalDeviceManagement.dto.RegisterRequest;
import com.example.medicalDeviceManagement.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    User register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request, HttpServletResponse response);
    String refreshAccessToken(String refreshToken, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    void changePassword(String id, ChangePasswordRequest request);
}
