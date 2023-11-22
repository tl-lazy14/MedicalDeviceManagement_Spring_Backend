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
@Document(collection = "purchaserequests")
public class PurchaseRequest {
    @Id
    private ObjectId id;

    @DBRef
    private User requester;
    private String deviceName;
    private Integer quantity;
    private Long unitPriceEstimated;
    private Long totalAmountEstimated;
    private Date dateOfRequest;
    private String status;
}
