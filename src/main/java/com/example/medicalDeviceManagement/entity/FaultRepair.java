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
@Document(collection = "faultrepairs")
public class FaultRepair {
    @Id
    private ObjectId id;

    @DBRef
    private Device device;
    @DBRef
    private User reporter;
    private Date time;
    private String description;
    private String repairStatus;
    private Date startDate;
    private Date finishedDate;
    private String repairServiceProvider;
    private Long cost;
}
