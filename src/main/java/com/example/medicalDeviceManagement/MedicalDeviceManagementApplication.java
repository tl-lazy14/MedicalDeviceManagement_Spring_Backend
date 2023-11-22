package com.example.medicalDeviceManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicalDeviceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalDeviceManagementApplication.class, args);
	}
}
