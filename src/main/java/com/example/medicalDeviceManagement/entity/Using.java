package com.example.medicalDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usings")
public class Using {
    @Id
    private ObjectId id;

    @DBRef
    private Device device;
    private String usageDepartment;
    @DBRef
    private User requester;
    private Date startDate;
    private Date endDate;
}
