package com.example.medicalDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "devices")
public class Device {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String deviceID;
    private String deviceName;
    @Indexed(unique = true)
    private String serialNumber;
    private String classification;
    private String manufacturer;
    private String origin;
    private Integer manufacturingYear;
    private Date importationDate;
    private Long price;
    private String storageLocation;
    private Date warrantyPeriod;
    private String maintenanceCycle;
}
