package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.PurchaseRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseRequestRepository extends MongoRepository<PurchaseRequest, ObjectId> {

}
