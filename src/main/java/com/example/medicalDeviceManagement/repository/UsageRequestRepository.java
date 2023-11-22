package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.UsingRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageRequestRepository extends MongoRepository<UsingRequest, ObjectId> {

}
