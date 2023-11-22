package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.FaultRepair;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FaultRepairRepository extends MongoRepository<FaultRepair, ObjectId> {

}
