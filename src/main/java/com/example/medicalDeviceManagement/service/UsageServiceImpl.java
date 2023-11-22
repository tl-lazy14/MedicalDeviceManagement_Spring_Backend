package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.User;
import com.example.medicalDeviceManagement.entity.Using;
import com.example.medicalDeviceManagement.entity.UsingRequest;
import com.example.medicalDeviceManagement.repository.DeviceRepository;
import com.example.medicalDeviceManagement.repository.UsageRequestRepository;
import com.example.medicalDeviceManagement.repository.UserRepository;
import com.example.medicalDeviceManagement.repository.UsingRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    private final UsingRepository usageRepository;
    private final UsageRequestRepository usageRequestRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final DeviceService deviceService;
    @Override
    public List<Using> getUsageHistoryOfDevice(String idDevice) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(new ObjectId(idDevice));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"));
        query.with(sort);
        return mongoTemplate.find(query, Using.class);
    }

    @Override
    public ListUsageRequestsResponse getUsageRequestByConditions(List<String> selectedUsageDepartment, List<String> selectedStatus, String selectedMonth, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedUsageDepartment.isEmpty()) {
            criteria = criteria.and("usageDepartment").in(selectedUsageDepartment);
        }
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
        if (selectedMonth != null) {
            String[] monthYear = selectedMonth.split("-");
            int year = Integer.parseInt(monthYear[0]);
            int month = Integer.parseInt(monthYear[1]);
            criteria = criteria.and("startDate").gte(LocalDate.of(year, month, 1))
                    .lt(LocalDate.of(year, month, 1).plusMonths(1));
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("endDate"));
        query.with(sort);
        Long totalRequests = mongoTemplate.count(query, UsingRequest.class);
        int totalPages = (int) Math.ceil((double) totalRequests / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<UsingRequest> requests = mongoTemplate.find(query, UsingRequest.class);
        List<UsageRequestDTO> responseList = requests.stream()
                .map(record -> UsageRequestDTO.builder()
                        ._id(record.getId().toString())
                        .requester(record.getRequester())
                        .usageDepartment(record.getUsageDepartment())
                        .deviceName(record.getDeviceName())
                        .quantity(record.getQuantity())
                        .startDate(record.getStartDate())
                        .endDate(record.getEndDate())
                        .status(record.getStatus())
                        .build()).toList();
        return ListUsageRequestsResponse.builder()
                .request(responseList)
                .totalRequests(totalRequests)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public ListUsageInfoResponse getUsageInfoByConditions(List<String> selectedUsageDepartment, String selectedMonth, String searchQuery, int page, int limit) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (!selectedUsageDepartment.isEmpty()) {
            criteriaList.add(Criteria.where("usageDepartment").in(selectedUsageDepartment));
        }
        List<User> users = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("userID").regex(searchQuery, "i"),
                        Criteria.where("name").regex(searchQuery, "i")
                )),
                User.class
        );
        List<Device> devices = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("deviceID").regex(searchQuery, "i"),
                        Criteria.where("deviceName").regex(searchQuery, "i")
                )),
                Device.class
        );
        List<ObjectId> idUser = users.stream().map(User::getId).toList();
        List<ObjectId> idDevice = devices.stream().map(Device::getId).toList();
        if (StringUtils.hasText(searchQuery)) {
            Criteria requesterOrDevice = new Criteria().orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("requester.$id").in(idUser)
            );
            criteriaList.add(requesterOrDevice);
        }
        if (selectedMonth != null) {
            String[] monthYear = selectedMonth.split("-");
            int year = Integer.parseInt(monthYear[0]);
            int month = Integer.parseInt(monthYear[1]);
            Criteria filterByMonth = new Criteria().orOperator(
                    Criteria.where("startDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1)),
                    Criteria.where("endDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1))
            );
            criteriaList.add(filterByMonth);
        }
        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        query.addCriteria(combinedCriteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("endDate"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, Using.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<Using> listUsageInfo = mongoTemplate.find(query, Using.class);
        List<UsageInfoDTO> responseList = listUsageInfo.stream()
                .map(record -> UsageInfoDTO.builder()
                        ._id(record.getId().toString())
                        .device(record.getDevice())
                        .usageDepartment(record.getUsageDepartment())
                        .requester(record.getRequester())
                        .startDate(record.getStartDate())
                        .endDate(record.getEndDate())
                        .build()).toList();
        return ListUsageInfoResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public MyUsageRequestResponse getMyUsageRequest(String id, List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("status").in(selectedStatus);
        }
        if (searchQuery != null) {
            criteria = criteria.orOperator(
                    Criteria.where("usageDepartment").regex(searchQuery, "i"),
                    Criteria.where("deviceName").regex(searchQuery, "i")
            );
        }
        criteria = criteria.and("requester.$id").is(new ObjectId(id));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("endDate"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, UsingRequest.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<UsingRequest> myUsageInfo = mongoTemplate.find(query, UsingRequest.class);
        List<UsageRequestDTO> responseList = myUsageInfo.stream()
                .map(record -> UsageRequestDTO.builder()
                        ._id(record.getId().toString())
                        .requester(record.getRequester())
                        .usageDepartment(record.getUsageDepartment())
                        .deviceName(record.getDeviceName())
                        .quantity(record.getQuantity())
                        .startDate(record.getStartDate())
                        .endDate(record.getEndDate())
                        .status(record.getStatus())
                        .build()).toList();
        return MyUsageRequestResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public List<String> getUsageDepartmentRequestForFilter() {
        List<String> usageDepartments = mongoTemplate.query(UsingRequest.class)
                .distinct("usageDepartment")
                .as(String.class)
                .all();
        Collections.sort(usageDepartments);
        return usageDepartments;
    }

    @Override
    public List<String> getUsageDepartment() {
        List<String> usageDepartments = mongoTemplate.query(Using.class)
                .distinct("usageDepartment")
                .as(String.class)
                .all();
        Collections.sort(usageDepartments);
        return usageDepartments;
    }

    @Override
    public ExportUsageInfoResponse getAllUsageInfoForExport(List<String> selectedUsageDepartment, String selectedMonth, String searchQuery) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (!selectedUsageDepartment.isEmpty()) {
            criteriaList.add(Criteria.where("usageDepartment").in(selectedUsageDepartment));
        }
        List<User> users = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("userID").regex(searchQuery, "i"),
                        Criteria.where("name").regex(searchQuery, "i")
                )),
                User.class
        );
        List<Device> devices = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("deviceID").regex(searchQuery, "i"),
                        Criteria.where("deviceName").regex(searchQuery, "i")
                )),
                Device.class
        );
        List<ObjectId> idUser = users.stream().map(User::getId).toList();
        List<ObjectId> idDevice = devices.stream().map(Device::getId).toList();
        if (StringUtils.hasText(searchQuery)) {
            Criteria requesterOrDevice = new Criteria().orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("requester.$id").in(idUser)
            );
            criteriaList.add(requesterOrDevice);
        }
        if (selectedMonth != null) {
            String[] monthYear = selectedMonth.split("-");
            int year = Integer.parseInt(monthYear[0]);
            int month = Integer.parseInt(monthYear[1]);
            Criteria filterByMonth = new Criteria().orOperator(
                    Criteria.where("startDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1)),
                    Criteria.where("endDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1))
            );
           criteriaList.add(filterByMonth);
        }
        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        query.addCriteria(combinedCriteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("endDate"));
        query.with(sort);
        List<Using> listUsageInfo = mongoTemplate.find(query, Using.class);
        return ExportUsageInfoResponse.builder().list(listUsageInfo).build();
    }

    @Override
    public void addUsageRequest(AddUsageRequest request) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        User requester = userRepository.findById(new ObjectId(request.getRequester())).orElseThrow();
        UsingRequest newUsageRequest = UsingRequest.builder()
                .requester(requester)
                .usageDepartment(request.getUsageDepartment())
                .deviceName(request.getDeviceName())
                .quantity(Integer.parseInt(request.getQuantity()))
                .startDate(dateFormat.parse(request.getStartDate()))
                .endDate(dateFormat.parse(request.getEndDate()))
                .status("Đang chờ duyệt")
                .build();
        usageRequestRepository.save(newUsageRequest);
    }

    @Override
    public void addUsageInfo(AddUsageInfo request) throws ParseException {
        List<DeviceUsageInfo> devices = request.getDevices();
        RequesterUsageInfo requester = request.getRequester();
        User getOperator = userRepository.findByUserID(requester.getUserID());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        for (DeviceUsageInfo device : devices) {
            Device getDevice = deviceRepository.findByDeviceID(device.getDeviceID());
            Using newUsageInfo = Using.builder()
                    .device(getDevice)
                    .usageDepartment(request.getUsageDepartment())
                    .requester(getOperator)
                    .startDate(dateFormat.parse(request.getStartDate()))
                    .endDate(dateFormat.parse(request.getEndDate()))
                    .build();
            usageRepository.save(newUsageInfo);
            DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
            DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
        }
    }

    @Override
    public List<UsageInfoDTO> getUsingDevice(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);
        Criteria criteria = Criteria.where("startDate").lte(end).and("endDate").gte(start);
        Query query = new Query(criteria);
        List<Using> listRecordOfDeviceInUse = mongoTemplate.find(query, Using.class);
        return listRecordOfDeviceInUse.stream()
                .map(record -> UsageInfoDTO.builder()
                        ._id(record.getId().toString())
                        .device(record.getDevice())
                        .usageDepartment(record.getUsageDepartment())
                        .requester(record.getRequester())
                        .startDate(record.getStartDate())
                        .endDate(record.getEndDate())
                        .build()).toList();
    }

    @Override
    public UsingRequest updateApproveStatus(String id, UpdateApproveStatusRequest request) {
        UsingRequest updatedApproveStatus = usageRequestRepository.findById(new ObjectId(id)).orElseThrow();
        updatedApproveStatus.setStatus(request.getStatus());
        usageRequestRepository.save(updatedApproveStatus);
        return updatedApproveStatus;
    }

    @Override
    public UsingRequest editUsageRequest(String id, UpdateUsageRequest request) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        UsingRequest updatedUsageRequest = usageRequestRepository.findById(new ObjectId(id)).orElseThrow();
        updatedUsageRequest.setUsageDepartment(request.getUsageDepartment());
        updatedUsageRequest.setDeviceName(request.getDeviceName());
        updatedUsageRequest.setQuantity(Integer.parseInt(request.getQuantity()));
        updatedUsageRequest.setStartDate(dateFormat.parse(request.getStartDate()));
        updatedUsageRequest.setEndDate(dateFormat.parse(request.getEndDate()));
        usageRequestRepository.save(updatedUsageRequest);
        return updatedUsageRequest;
    }

    @Override
    public Using updateUsageInfo(String id, UpdateUsageInfo request) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Device getDevice = deviceRepository.findByDeviceID(request.getDevice().getDeviceID());
        User getRequester = userRepository.findByUserID(request.getRequester().getUserID());
        Using updatedUsageInfo = usageRepository.findById(new ObjectId(id)).orElseThrow();
        updatedUsageInfo.setDevice(getDevice);
        updatedUsageInfo.setUsageDepartment(request.getUsageDepartment());
        updatedUsageInfo.setRequester(getRequester);
        updatedUsageInfo.setStartDate(dateFormat.parse(request.getStartDate()));
        updatedUsageInfo.setEndDate(dateFormat.parse(request.getEndDate()));
        usageRepository.save(updatedUsageInfo);
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
        return updatedUsageInfo;
    }

    @Override
    public UsingRequest deleteUsageRequest(String id) {
        UsingRequest deletedUsageRequest = usageRequestRepository.findById(new ObjectId(id)).orElseThrow();
        usageRequestRepository.deleteById(new ObjectId(id));
        return deletedUsageRequest;
    }

    @Override
    public Using deleteUsageInfo(String id) {
        Using deletedUsageInfo = usageRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi sử dụng."));
        usageRepository.deleteById(new ObjectId(id));
        DeviceServiceImpl.usageStatus.remove(deletedUsageInfo.getDevice().getId().toString());
        DeviceServiceImpl.usageStatus.put(deletedUsageInfo.getDevice().getId().toString(), deviceService.calculateUsageStatus(deletedUsageInfo.getDevice()));
        return deletedUsageInfo;
    }
}

