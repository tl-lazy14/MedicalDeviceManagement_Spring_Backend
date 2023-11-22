package com.example.medicalDeviceManagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/login/**", "/api/auth/refresh/**").permitAll()
                .requestMatchers("/api/auth/register/**",
                                 "/api/device/get-list-device",
                                 "/api/device/devices/**",
                                 "/api/device/export",
                                 "/api/device/manufacturers",
                                 "/api/device/storage-locations",
                                 "/api/device/maintenance/due",
                                 "/api/user",
                                 "/api/user/info/**",
                                 "/api/user/getName/**",
                                 "/api/usage/history/**",
                                 "/api/usage/request/department",
                                 "/api/usage/request/list-request",
                                 "/api/usage/request/status/**",
                                 "/api/usage/info/**",
                                 "/api/usage/export",
                                 "/api/faultRepair/{idDevice}",
                                 "/api/faultRepair/fault/statusFault",
                                 "/api/faultRepair/fault/list",
                                 "/api/faultRepair/fault-repair/export",
                                 "/api/faultRepair/repair/**",
                                 "/api/maintenance/**",
                                 "/api/purchase-request/list-request",
                                 "/api/purchase-request/export",
                                 "/api/purchase-request/status/**")
                                .hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/user/{id}").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/auth/change-password/**",
                                 "/api/usage/request/operator/**",
                                 "/api/usage/request/create",
                                 "/api/usage/request/crud/**",
                                 "/api/faultRepair/fault/operator/**",
                                 "/api/faultRepair/fault/report/**",
                                 "/api/purchase-request/operator/**",
                                 "/api/purchase-request/create",
                                 "/api/purchase-request/crud/**")
                                .hasAuthority("ROLE_USER")
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
