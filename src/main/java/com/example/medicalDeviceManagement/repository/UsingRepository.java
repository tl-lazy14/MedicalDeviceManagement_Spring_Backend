package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.Using;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsingRepository extends MongoRepository<Using, ObjectId> {

}
