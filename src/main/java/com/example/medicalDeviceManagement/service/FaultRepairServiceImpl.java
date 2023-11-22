package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.FaultRepair;
import com.example.medicalDeviceManagement.entity.User;
import com.example.medicalDeviceManagement.repository.DeviceRepository;
import com.example.medicalDeviceManagement.repository.FaultRepairRepository;
import com.example.medicalDeviceManagement.repository.UserRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class FaultRepairServiceImpl implements FaultRepairService {
    private final FaultRepairRepository faultRepairRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    private final DeviceService deviceService;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<FaultRepair> getFaultRepairHistoryOfDevice(String idDevice) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(new ObjectId(idDevice));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("time"));
        query.with(sort);
        return mongoTemplate.find(query, FaultRepair.class);
    }

    @Override
    public List<Device> getFaultDevice() {
        List<Device> allDevices = deviceRepository.findAll();
        return allDevices.stream()
                .filter(device -> Objects.equals(DeviceServiceImpl.usageStatus.get(device.getId().toString()), "Hỏng"))
                .toList();
    }

    @Override
    public List<FaultRepair> getReparingDevice(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);
        Criteria criteria = Criteria.where("startDate").lte(end).and("finishedDate").gte(start);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, FaultRepair.class);
    }

    @Override
    public ListFaultRepairResponse getFaultInfoByConditions(List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("repairStatus").in(selectedStatus);
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
            criteria = criteria.orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("reporter.$id").in(idUser),
                    Criteria.where("description").regex(searchQuery, "i")
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("time"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, FaultRepair.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<FaultRepair> faultReport = mongoTemplate.find(query, FaultRepair.class);
        List<FaultRepairDTO> responseList = faultReport.stream()
                .map(record -> FaultRepairDTO.builder()
                        ._id(record.getId().toString())
                        .device(record.getDevice())
                        .reporter(record.getReporter())
                        .time(record.getTime())
                        .description(record.getDescription())
                        .repairStatus(record.getRepairStatus())
                        .startDate(record.getStartDate())
                        .finishedDate(record.getFinishedDate())
                        .repairServiceProvider(record.getRepairServiceProvider())
                        .cost(record.getCost())
                        .build()).toList();
        return ListFaultRepairResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public ExportFaultRepairResponse getFaultRepairForExport(List<String> selectedStatus, String searchQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("repairStatus").in(selectedStatus);
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
            criteria = criteria.orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("reporter.$id").in(idUser),
                    Criteria.where("description").regex(searchQuery, "i")
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("time"));
        query.with(sort);
        List<FaultRepair> listFaultReport = mongoTemplate.find(query, FaultRepair.class);
        return ExportFaultRepairResponse.builder().list(listFaultReport).build();
    }

    @Override
    public ListFaultRepairResponse getMyFaultReport(String id, List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedStatus.isEmpty()) {
            criteria = criteria.and("repairStatus").in(selectedStatus);
        }
        List<Device> devices = mongoTemplate.find(
                Query.query(new Criteria().orOperator(
                        Criteria.where("deviceID").regex(searchQuery, "i"),
                        Criteria.where("deviceName").regex(searchQuery, "i")
                )),
                Device.class
        );
        List<ObjectId> idDevice = devices.stream().map(Device::getId).toList();
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("description").regex(searchQuery, "i")
            );
        }
        criteria = criteria.and("reporter.$id").is(new ObjectId(id));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("time"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, FaultRepair.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<FaultRepair> result = mongoTemplate.find(query, FaultRepair.class);
        List<FaultRepairDTO> responseList = result.stream()
                .map(record -> FaultRepairDTO.builder()
                        ._id(record.getId().toString())
                        .device(record.getDevice())
                        .reporter(record.getReporter())
                        .time(record.getTime())
                        .description(record.getDescription())
                        .repairStatus(record.getRepairStatus())
                        .startDate(record.getStartDate())
                        .finishedDate(record.getFinishedDate())
                        .repairServiceProvider(record.getRepairServiceProvider())
                        .cost(record.getCost())
                        .build()).toList();
        return ListFaultRepairResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public void addFaultReport(AddFaultReport report) throws ParseException {
        Device getDevice = deviceRepository.findByDeviceID(report.getDevice().getDeviceID());
        User reporter = userRepository.findById(new ObjectId(report.getReporter())).orElseThrow();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String time = report.getTime().replace('T', ' ');
        FaultRepair newReport = FaultRepair.builder()
                .device(getDevice)
                .reporter(reporter)
                .time(dateFormat.parse(time))
                .description(report.getDescription())
                .repairStatus("Chờ quyết định")
                .build();
        faultRepairRepository.save(newReport);
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
    }

    @Override
    public FaultRepair updateRepairDecision(String id, UpdateRepairDecision repairDecision) {
        FaultRepair updatedRepairDecision = faultRepairRepository.findById(new ObjectId(id)).orElseThrow();
        updatedRepairDecision.setRepairStatus(repairDecision.getRepairStatus());
        faultRepairRepository.save(updatedRepairDecision);
        Device getDevice = deviceRepository.findByDeviceID(updatedRepairDecision.getDevice().getDeviceID());
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
        return updatedRepairDecision;
    }

    @Override
    public FaultRepair updateRepairInfo(String id, UpdateRepairInfo request) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        FaultRepair updatedRepairInfo = faultRepairRepository.findById(new ObjectId(id)).orElseThrow();
        updatedRepairInfo.setStartDate(dateFormat.parse(request.getStartDate()));
        updatedRepairInfo.setFinishedDate(dateFormat.parse(request.getFinishedDate()));
        updatedRepairInfo.setRepairServiceProvider(request.getRepairServiceProvider());
        updatedRepairInfo.setCost(Long.parseLong(request.getCost()));
        faultRepairRepository.save(updatedRepairInfo);
        Device getDevice = deviceRepository.findByDeviceID(updatedRepairInfo.getDevice().getDeviceID());
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
        return updatedRepairInfo;
    }

    @Override
    public FaultRepair editFaultReport(String id, UpdateFaultReport report) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String time = report.getTime().replace('T', ' ');
        FaultRepair updatedFaultReport = faultRepairRepository.findById(new ObjectId(id)).orElseThrow();
        updatedFaultReport.setTime(dateFormat.parse(time));
        updatedFaultReport.setDescription(report.getDescription());
        faultRepairRepository.save(updatedFaultReport);
        return updatedFaultReport;
    }
}
