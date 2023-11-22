package com.example.medicalDeviceManagement.repository;

import com.example.medicalDeviceManagement.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    boolean existsByUserID(String userID);
    boolean existsByEmail(String email);
    User findByUserID(String userID);
}
