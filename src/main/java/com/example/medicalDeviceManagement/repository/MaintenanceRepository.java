package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.Maintenance;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaintenanceRepository extends MongoRepository<Maintenance, ObjectId> {

}
