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
@Document(collection = "maintenances")
public class Maintenance {
    @Id
    private ObjectId id;

    @DBRef
    private Device device;
    private Date startDate;
    private Date finishedDate;
    private String performer;
    private Long cost;
    private String maintenanceServiceProvider;
}
