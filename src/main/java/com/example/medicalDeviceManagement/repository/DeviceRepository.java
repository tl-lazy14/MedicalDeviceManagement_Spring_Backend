package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.Device;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<Device, ObjectId> {
    boolean existsByDeviceID(String deviceID);
    Device findByDeviceID(String deviceID);
}
