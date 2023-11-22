package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.PurchaseRequest;
import com.example.medicalDeviceManagement.entity.User;
import com.example.medicalDeviceManagement.repository.PurchaseRequestRepository;
import com.example.medicalDeviceManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public ListPurchaseRequestResponse getPurchaseRequestByConditions(List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("status").in(selectedStatus);
        }
        List<User> users = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("userID").regex(searchQuery, "i"),
                        Criteria.where("name").regex(searchQuery, "i")
                )),
                User.class
        );
        List<ObjectId> idUser = users.stream().map(User::getId).toList();
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("deviceName").regex(searchQuery, "i"),
                    Criteria.where("requester.$id").in(idUser)
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("dateOfRequest"));
        query.with(sort);
        Long totalRequests = mongoTemplate.count(query, PurchaseRequest.class);
        int totalPages = (int) Math.ceil((double) totalRequests / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<PurchaseRequest> purchaseRequests = mongoTemplate.find(query, PurchaseRequest.class);
        List<PurchaseRequestDTO> responseList = purchaseRequests.stream()
                .map(record -> PurchaseRequestDTO.builder()
                        ._id(record.getId().toString())
                        .requester(record.getRequester())
                        .deviceName(record.getDeviceName())
                        .quantity(record.getQuantity())
                        .unitPriceEstimated(record.getUnitPriceEstimated())
                        .totalAmountEstimated(record.getTotalAmountEstimated())
                        .dateOfRequest(record.getDateOfRequest())
                        .status(record.getStatus())
                        .build()).toList();
        return ListPurchaseRequestResponse.builder()
                .list(responseList)
                .totalRequests(totalRequests)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public ExportPurchaseRequestResponse getPurchaseRequestForExport(List<String> selectedStatus, String searchQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("status").in(selectedStatus);
        }
        List<User> users = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("userID").regex(searchQuery, "i"),
                        Criteria.where("name").regex(searchQuery, "i")
                )),
                User.class
        );
        List<ObjectId> idUser = users.stream().map(User::getId).toList();
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("deviceName").regex(searchQuery, "i"),
                    Criteria.where("requester.$id").in(idUser)
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("dateOfRequest"));
        query.with(sort);
        List<PurchaseRequest> listPurchaseRequests = mongoTemplate.find(query, PurchaseRequest.class);
        return ExportPurchaseRequestResponse.builder().list(listPurchaseRequests).build();
    }

    @Override
    public MyPurchaseRequestResponse getMyPurchaseRequest(String id, List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("status").in(selectedStatus);
        }
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.and("deviceName").regex(searchQuery, "i");
        }
        criteria = criteria.and("requester.$id").is(new ObjectId(id));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("dateOfRequest"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, PurchaseRequest.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<PurchaseRequest> requests = mongoTemplate.find(query, PurchaseRequest.class);
        List<PurchaseRequestDTO> responseList = requests.stream()
                .map(record -> PurchaseRequestDTO.builder()
                        ._id(record.getId().toString())
                        .requester(record.getRequester())
                        .deviceName(record.getDeviceName())
                        .quantity(record.getQuantity())
                        .unitPriceEstimated(record.getUnitPriceEstimated())
                        .totalAmountEstimated(record.getTotalAmountEstimated())
                        .dateOfRequest(record.getDateOfRequest())
                        .status(record.getStatus())
                        .build()).toList();
        return MyPurchaseRequestResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public void addPurchaseRequest(AddPurchaseRequest purchaseRequest) {
        User requester = userRepository.findById(new ObjectId(purchaseRequest.getRequester())).orElseThrow();
        PurchaseRequest newRequest = PurchaseRequest.builder()
                .requester(requester)
                .deviceName(purchaseRequest.getDeviceName())
                .quantity(Integer.parseInt(purchaseRequest.getQuantity()))
                .unitPriceEstimated(Long.parseLong(purchaseRequest.getUnitPriceEstimated()))
                .totalAmountEstimated(Long.parseLong(purchaseRequest.getUnitPriceEstimated()) * Integer.parseInt(purchaseRequest.getQuantity()))
                .dateOfRequest(new Date())
                .status("Đang chờ duyệt")
                .build();
        purchaseRequestRepository.save(newRequest);
    }

    @Override
    public PurchaseRequest updateApproveStatus(String id, UpdateApproveStatusRequest status) {
        PurchaseRequest purchaseRequest = purchaseRequestRepository.findById(new ObjectId(id)).orElseThrow();
        purchaseRequest.setStatus(status.getStatus());
        purchaseRequestRepository.save(purchaseRequest);
        return purchaseRequest;
    }

    @Override
    public PurchaseRequest editPurchaseRequest(String id, UpdatePurchaseRequest request) {
        PurchaseRequest purchaseRequest = purchaseRequestRepository.findById(new ObjectId(id)).orElseThrow();
        purchaseRequest.setDeviceName(request.getDeviceName());
        purchaseRequest.setQuantity(Integer.parseInt(request.getQuantity()));
        purchaseRequest.setUnitPriceEstimated(Long.parseLong(request.getUnitPriceEstimated()));
        purchaseRequest.setTotalAmountEstimated(Long.parseLong(request.getUnitPriceEstimated()) * Integer.parseInt(request.getQuantity()));
        purchaseRequest.setDateOfRequest(new Date());
        purchaseRequestRepository.save(purchaseRequest);
        return purchaseRequest;
    }

    @Override
    public PurchaseRequest deletePurchaseRequest(String id) {
        PurchaseRequest purchaseRequest = purchaseRequestRepository.findById(new ObjectId(id)).orElseThrow();
        purchaseRequestRepository.deleteById(new ObjectId(id));
        return purchaseRequest;
    }
}
