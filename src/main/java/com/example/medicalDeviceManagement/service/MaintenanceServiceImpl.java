package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.ExportMaintenanceResponse;
import com.example.medicalDeviceManagement.dto.ListMaintenanceResponse;
import com.example.medicalDeviceManagement.dto.MaintenanceDTO;
import com.example.medicalDeviceManagement.dto.MaintenanceInfoDTO;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.Maintenance;
import com.example.medicalDeviceManagement.repository.DeviceRepository;
import com.example.medicalDeviceManagement.repository.MaintenanceRepository;
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
public class MaintenanceServiceImpl implements MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final DeviceRepository deviceRepository;

    private final MongoTemplate mongoTemplate;
    private final DeviceService deviceService;

    @Override
    public List<Maintenance> getMaintenanceHistoryOfDevice(String idDevice) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(new ObjectId(idDevice));
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"));
        query.with(sort);
        return mongoTemplate.find(query, Maintenance.class);
    }

    @Override
    public List<Maintenance> getMaintenancingDevice(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date start = dateFormat.parse(startDate);
        Date end = dateFormat.parse(endDate);
        Criteria criteria = Criteria.where("startDate").lte(end).and("finishedDate").gte(start);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Maintenance.class);
    }

    @Override
    public List<String> getServiceProvider() {
        List<String> providers = mongoTemplate.query(Maintenance.class)
                .distinct("maintenanceServiceProvider")
                .as(String.class)
                .all();
        Collections.sort(providers);
        return providers;
    }

    @Override
    public ListMaintenanceResponse getMaintenanceInfoByConditions(List<String> selectedProviders, String selectedMonth, String searchQuery, int page, int limit) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (!selectedProviders.isEmpty()) {
            criteriaList.add(Criteria.where("maintenanceServiceProvider").in(selectedProviders));
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
            Criteria filterByDeviceOrPerformer = new Criteria().orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("performer").regex(searchQuery, "i")
            );
            criteriaList.add(filterByDeviceOrPerformer);
        }
        if (selectedMonth != null) {
            String[] monthYear = selectedMonth.split("-");
            int year = Integer.parseInt(monthYear[0]);
            int month = Integer.parseInt(monthYear[1]);
            Criteria filterByMonth = new Criteria().orOperator(
                    Criteria.where("startDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1)),
                    Criteria.where("finishedDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1))
            );
            criteriaList.add(filterByMonth);
        }
        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        query.addCriteria(combinedCriteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("finishedDate"));
        query.with(sort);
        Long totalRecords = mongoTemplate.count(query, Maintenance.class);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        query.skip((long) (page - 1) * limit);
        query.limit(limit);
        List<Maintenance> maintenanceInfo = mongoTemplate.find(query, Maintenance.class);
        List<MaintenanceDTO> responseList = maintenanceInfo.stream()
                .map(record -> MaintenanceDTO.builder()
                        ._id(record.getId().toString())
                        .device(record.getDevice())
                        .startDate(record.getStartDate())
                        .finishedDate(record.getFinishedDate())
                        .performer(record.getPerformer())
                        .cost(record.getCost())
                        .maintenanceServiceProvider(record.getMaintenanceServiceProvider())
                        .build()).toList();
        return ListMaintenanceResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public ExportMaintenanceResponse getAllMaintenanceInfoForExport(List<String> selectedProviders, String selectedMonth, String searchQuery) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (!selectedProviders.isEmpty()) {
            criteriaList.add(Criteria.where("maintenanceServiceProvider").in(selectedProviders));
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
            Criteria filterByDeviceOrPerformer = new Criteria().orOperator(
                    Criteria.where("device.$id").in(idDevice),
                    Criteria.where("performer").regex(searchQuery, "i")
            );
            criteriaList.add(filterByDeviceOrPerformer);
        }
        if (selectedMonth != null) {
            String[] monthYear = selectedMonth.split("-");
            int year = Integer.parseInt(monthYear[0]);
            int month = Integer.parseInt(monthYear[1]);
            Criteria filterByMonth = new Criteria().orOperator(
                    Criteria.where("startDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1)),
                    Criteria.where("finishedDate").gte(LocalDate.of(year, month, 1))
                            .lt(LocalDate.of(year, month, 1).plusMonths(1))
            );
            criteriaList.add(filterByMonth);
        }
        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        query.addCriteria(combinedCriteria);
        Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("finishedDate"));
        query.with(sort);
        List<Maintenance> listMaintenanceInfo = mongoTemplate.find(query, Maintenance.class);
        return ExportMaintenanceResponse.builder().list(listMaintenanceInfo).build();
    }

    @Override
    public void addMaintenanceInfo(MaintenanceInfoDTO request) throws ParseException {
        Device getDevice = deviceRepository.findByDeviceID(request.getDevice().getDeviceID());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Maintenance newMaintenanceInfo = Maintenance.builder()
                .device(getDevice)
                .startDate(dateFormat.parse(request.getStartDate()))
                .finishedDate(dateFormat.parse(request.getFinishedDate()))
                .performer(request.getPerformer())
                .cost(Long.parseLong(request.getCost()))
                .maintenanceServiceProvider(request.getMaintenanceServiceProvider())
                .build();
        maintenanceRepository.save(newMaintenanceInfo);
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
    }

    @Override
    public Maintenance updateMaintenanceInfo(String id, MaintenanceInfoDTO request) throws ParseException {
        Device getDevice = deviceRepository.findByDeviceID(request.getDevice().getDeviceID());
        Maintenance updatedMaintenanceInfo = maintenanceRepository.findById(new ObjectId(id)).orElseThrow();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        updatedMaintenanceInfo.setDevice(getDevice);
        updatedMaintenanceInfo.setStartDate(dateFormat.parse(request.getStartDate()));
        updatedMaintenanceInfo.setFinishedDate(dateFormat.parse(request.getFinishedDate()));
        updatedMaintenanceInfo.setPerformer(request.getPerformer());
        updatedMaintenanceInfo.setMaintenanceServiceProvider(request.getMaintenanceServiceProvider());
        updatedMaintenanceInfo.setCost(Long.parseLong(request.getCost()));
        maintenanceRepository.save(updatedMaintenanceInfo);
        DeviceServiceImpl.usageStatus.remove(getDevice.getId().toString());
        DeviceServiceImpl.usageStatus.put(getDevice.getId().toString(), deviceService.calculateUsageStatus(getDevice));
        return updatedMaintenanceInfo;
    }

    @Override
    public Maintenance deleteMaintenanceInfo(String id) {
        Maintenance deletedMaintenanceInfoRecord = maintenanceRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi."));
        maintenanceRepository.deleteById(new ObjectId(id));
        DeviceServiceImpl.usageStatus.remove(deletedMaintenanceInfoRecord.getDevice().getId().toString());
        DeviceServiceImpl.usageStatus.put(deletedMaintenanceInfoRecord.getDevice().getId().toString(), deviceService.calculateUsageStatus(deletedMaintenanceInfoRecord.getDevice()));
        return  deletedMaintenanceInfoRecord;
    }
}
