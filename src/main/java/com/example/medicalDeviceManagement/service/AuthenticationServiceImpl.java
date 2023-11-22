package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.AuthenticationResponse;
import com.example.medicalDeviceManagement.dto.ChangePasswordRequest;
import com.example.medicalDeviceManagement.dto.LoginRequest;
import com.example.medicalDeviceManagement.dto.RegisterRequest;
import com.example.medicalDeviceManagement.entity.User;
import com.example.medicalDeviceManagement.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private static List<String> refreshTokens = new ArrayList<>();
    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByUserID(request.getUserID())) {
            throw new RuntimeException("Mã người vận hành đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        User user = User.builder()
                .userID(request.getUserID())
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .isAdmin(false)
                .department(request.getDepartment())
                .build();
        userRepository.save(user);
        return user;
    }

    @Override
    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokens.add(refreshToken);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(false);
        response.addCookie(refreshTokenCookie);
        return AuthenticationResponse.builder()
                ._id(user.getId().toString())
                .userID(user.getUserID())
                .email(user.getEmail())
                .name(user.getName())
                .isAdmin(user.isAdmin())
                .department(user.getDepartment())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        if (!refreshTokens.contains(refreshToken)) {
            throw new RuntimeException("Refresh token is not valid!");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        if (jwtService.isTokenValid(refreshToken, userDetails)) {
            refreshTokens = refreshTokens.stream()
                    .filter(token -> !token.equals(refreshToken))
                    .collect(Collectors.toList());
            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);
            refreshTokens.add(newRefreshToken);
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setSecure(false);
            response.addCookie(refreshTokenCookie);
            return newAccessToken;
        } else {
            throw new RuntimeException("Refresh token is not valid!");
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    String refreshToken = cookie.getValue();
                    refreshTokens = refreshTokens.stream()
                            .filter(token -> !token.equals(refreshToken))
                            .collect(Collectors.toList());
                }
            }
        }
    }

    @Override
    public void changePassword(String id, ChangePasswordRequest request) {
        User user = userRepository.findById(new ObjectId(id)).orElseThrow();
        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        String newPassword = request.getNewPassword();
        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);

        user.setPassword(hashedNewPassword);
        userRepository.save(user);
    }
}
