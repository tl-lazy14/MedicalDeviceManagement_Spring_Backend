package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.*;
import com.example.medicalDeviceManagement.entity.Device;
import com.example.medicalDeviceManagement.entity.FaultRepair;
import com.example.medicalDeviceManagement.entity.Maintenance;
import com.example.medicalDeviceManagement.entity.Using;
import com.example.medicalDeviceManagement.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    public static final Map<String, String> usageStatus = new HashMap<>();
    private final MongoTemplate mongoTemplate;

    @Override
    public String getUsageStatus(Device device) {
        String id = device.getId().toString();
        if (usageStatus.containsKey(id)) {
            return usageStatus.get(id);
        }
        String status = calculateUsageStatus(device);
        usageStatus.put(id, status);
        return status;
    }
    @Override
    public String calculateUsageStatus(Device device) {
        if (isDeviceFaulty(device)) {
            return "Hỏng";
        }
        if (isDeviceRepairing(device)) {
            return "Đang sửa chữa";
        }
        if (isDeviceUnderMaintenance(device)) {
            return "Đang bảo trì";
        }
        if (isDeviceInUse(device)) {
            return "Đang sử dụng";
        }
        return "Sẵn sàng sử dụng";
    }

    public boolean isDeviceFaulty(Device device) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(device.getId());
        query.addCriteria(criteria);
        List<FaultRepair> faultRepairs = mongoTemplate.find(query, FaultRepair.class);
        for (FaultRepair record : faultRepairs) {
            if (record.getRepairStatus().equals("Chờ quyết định") ||
                record.getRepairStatus().equals("Không sửa") ||
                    (record.getRepairStatus().equals("Sửa") && record.getStartDate() == null)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeviceRepairing(Device device) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(device.getId());
        query.addCriteria(criteria);
        List<FaultRepair> faultRepairs = mongoTemplate.find(query, FaultRepair.class);
        Date currentDate = new Date();
        for (FaultRepair record : faultRepairs) {
            if (record.getRepairStatus().equals("Sửa") &&
                    (record.getStartDate().before(currentDate) || record.getStartDate().equals(currentDate)) &&
                    (record.getFinishedDate() == null || record.getFinishedDate().after(currentDate) || record.getFinishedDate().equals(currentDate))) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeviceUnderMaintenance(Device device) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(device.getId());
        query.addCriteria(criteria);
        List<Maintenance> maintenances = mongoTemplate.find(query, Maintenance.class);
        Date currentDate = new Date();
        for (Maintenance record : maintenances) {
            if ((record.getStartDate().before(currentDate) || record.getStartDate().equals(currentDate)) &&
                    (record.getFinishedDate() == null || record.getFinishedDate().after(currentDate) || record.getFinishedDate().equals(currentDate))) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeviceInUse(Device device) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria = criteria.and("device.$id").is(device.getId());
        query.addCriteria(criteria);
        List<Using> usages = mongoTemplate.find(query, Using.class);
        Date currentDate = new Date();
        for (Using record : usages) {
            if ((record.getStartDate().before(currentDate) || record.getStartDate().equals(currentDate)) &&
                    (record.getEndDate().after(currentDate) || record.getEndDate().equals(currentDate))) {
                return true;
            }
        }
        return false;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy hàng ngày vào 00:00:00
    public void resetUsageStatusDaily() {
        usageStatus.clear();
    }

    @Override
    public ListDeviceResponse getDevicesByConditions(List<String> selectedType, List<String> selectedManufacturer, List<String> selectedStorageLocation, List<String> selectedStatus, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedType.isEmpty()) {
            criteria = criteria.and("classification").in(selectedType);
        }
        if (!selectedManufacturer.isEmpty()) {
            criteria = criteria.and("manufacturer").in(selectedManufacturer);
        }
        if (!selectedStorageLocation.isEmpty()) {
            criteria = criteria.and("storageLocation").in(selectedStorageLocation);
        }
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("deviceID").regex(searchQuery, "i"),
                    Criteria.where("deviceName").regex(searchQuery, "i")
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("importationDate"));
        query.with(sort);
        List<Device> devices = mongoTemplate.find(query, Device.class);
        List<DeviceDTO> devicesWithUsageStatus = devices.stream()
                .map(device -> DeviceDTO.builder()
                        ._id(device.getId().toString())
                        .deviceID(device.getDeviceID())
                        .deviceName(device.getDeviceName())
                        .serialNumber(device.getSerialNumber())
                        .classification(device.getClassification())
                        .manufacturer(device.getManufacturer())
                        .origin(device.getOrigin())
                        .manufacturingYear(device.getManufacturingYear())
                        .importationDate(device.getImportationDate())
                        .price(device.getPrice())
                        .storageLocation(device.getStorageLocation())
                        .warrantyPeriod(device.getWarrantyPeriod())
                        .maintenanceCycle(device.getMaintenanceCycle())
                        .usageStatus(getUsageStatus(device))
                        .build()).toList();
        List<DeviceDTO> filteredDeviceDTOs;
        if (!selectedStatus.isEmpty()) {
            filteredDeviceDTOs = devicesWithUsageStatus.stream()
                    .filter(dto -> selectedStatus.contains(dto.getUsageStatus()))
                    .collect(Collectors.toList());
        } else {
            filteredDeviceDTOs = devicesWithUsageStatus;
        }
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, filteredDeviceDTOs.size());
        List<DeviceDTO> listDevices = filteredDeviceDTOs.subList(startIndex, endIndex);
        return ListDeviceResponse.builder()
                .devices(listDevices)
                .totalDevices((long) filteredDeviceDTOs.size())
                .totalPages((int) Math.ceil((double) filteredDeviceDTOs.size() / limit))
                .build();
    }

    @Override
    public ExportDeviceResponse getAllDevicesForExport(List<String> selectedType, List<String> selectedManufacturer, List<String> selectedStorageLocation, List<String> selectedStatus, String searchQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!selectedType.isEmpty()) {
            criteria = criteria.and("classification").in(selectedType);
        }
        if (!selectedManufacturer.isEmpty()) {
            criteria = criteria.and("manufacturer").in(selectedManufacturer);
        }
        if (!selectedStorageLocation.isEmpty()) {
            criteria = criteria.and("storageLocation").in(selectedStorageLocation);
        }
        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("deviceID").regex(searchQuery, "i"),
                    Criteria.where("deviceName").regex(searchQuery, "i")
            );
        }
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Order.desc("importationDate"));
        query.with(sort);
        List<Device> devices = mongoTemplate.find(query, Device.class);
        List<DeviceDTO> devicesWithUsageStatus = devices.stream()
                .map(device -> DeviceDTO.builder()
                        ._id(device.getId().toString())
                        .deviceID(device.getDeviceID())
                        .deviceName(device.getDeviceName())
                        .serialNumber(device.getSerialNumber())
                        .classification(device.getClassification())
                        .manufacturer(device.getManufacturer())
                        .origin(device.getOrigin())
                        .manufacturingYear(device.getManufacturingYear())
                        .importationDate(device.getImportationDate())
                        .price(device.getPrice())
                        .storageLocation(device.getStorageLocation())
                        .warrantyPeriod(device.getWarrantyPeriod())
                        .maintenanceCycle(device.getMaintenanceCycle())
                        .usageStatus(getUsageStatus(device))
                        .build()).toList();
        List<DeviceDTO> filteredDeviceDTOs;
        if (!selectedStatus.isEmpty()) {
            filteredDeviceDTOs = devicesWithUsageStatus.stream()
                    .filter(dto -> selectedStatus.contains(dto.getUsageStatus()))
                    .collect(Collectors.toList());
        } else {
            filteredDeviceDTOs = devicesWithUsageStatus;
        }
        return ExportDeviceResponse.builder().devices(filteredDeviceDTOs).build();
    }

    @Override
    public List<String> getManufacturerForFilter() {
        List<String> manufacturers = mongoTemplate.query(Device.class)
                .distinct("manufacturer")
                .as(String.class)
                .all();
        Collections.sort(manufacturers);
        return manufacturers;
    }

    @Override
    public List<String> getStorageForFilter() {
        List<String> storageLocations = mongoTemplate.query(Device.class)
                .distinct("storageLocation")
                .as(String.class)
                .all();
        Collections.sort(storageLocations);
        return storageLocations;
    }

    @Override
    public DeviceDTO getDeviceByID(String id) {
        Device device = deviceRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        return DeviceDTO.builder()
                ._id(device.getId().toString())
                .deviceID(device.getDeviceID())
                .deviceName(device.getDeviceName())
                .serialNumber(device.getSerialNumber())
                .classification(device.getClassification())
                .manufacturer(device.getManufacturer())
                .origin(device.getOrigin())
                .manufacturingYear(device.getManufacturingYear())
                .importationDate(device.getImportationDate())
                .price(device.getPrice())
                .storageLocation(device.getStorageLocation())
                .warrantyPeriod(device.getWarrantyPeriod())
                .maintenanceCycle(device.getMaintenanceCycle())
                .usageStatus(getUsageStatus(device))
                .build();
    }

    @Override
    public String getDeviceNameByID(String deviceID) {
        Device device = deviceRepository.findByDeviceID(deviceID);
        if (device == null) {
            return null;
        } else {
            return device.getDeviceName();
        }
    }

    @Override
    public List<PeriodicMaintenanceInfo> getDevicesDueForMaintenance() {
        Date currentDate = new Date();
        Date twoWeeksFromNow = new Date(currentDate.getTime() + 14 * 24 * 60 * 60 *1000);
        List<Device> devices = deviceRepository.findAll();
        List<PeriodicMaintenanceInfo> periodicMaintenanceInfos = new ArrayList<>();
        for (Device device : devices) {
            Date nextMaintenanceDate = new Date(device.getImportationDate().getTime());
            int cycle = Integer.parseInt(device.getMaintenanceCycle().split(" ")[0]);
            while (nextMaintenanceDate.before(currentDate)) {
                nextMaintenanceDate = new Date(nextMaintenanceDate.getTime() + (long) cycle * 30 * 24 * 60 * 60 * 1000);
            }
            if (nextMaintenanceDate.before(twoWeeksFromNow) || nextMaintenanceDate.equals(twoWeeksFromNow)) {
                PeriodicMaintenanceInfo info = PeriodicMaintenanceInfo.builder()
                        .deviceID(device.getDeviceID())
                        .deviceName(device.getDeviceName())
                        .dueForMaintenance(true)
                        .date(nextMaintenanceDate)
                        .build();
                periodicMaintenanceInfos.add(info);
            }
        }
        periodicMaintenanceInfos.sort(Comparator.comparing(PeriodicMaintenanceInfo::getDate));
        return periodicMaintenanceInfos;
    }

    @Override
    public Device addDevice(DeviceRequest request) throws ParseException {
        String deviceID = request.getDeviceID();
        if (deviceRepository.existsByDeviceID(deviceID)) {
            throw new IllegalArgumentException("Mã thiết bị đã tồn tại");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Device newDevice = Device.builder()
                .deviceID(request.getDeviceID())
                .deviceName(request.getDeviceName())
                .serialNumber(request.getSerialNumber())
                .classification(request.getClassification())
                .manufacturer(request.getManufacturer())
                .origin(request.getOrigin())
                .manufacturingYear(Integer.parseInt(request.getManufacturingYear()))
                .importationDate(dateFormat.parse(request.getImportationDate()))
                .price(Long.parseLong(request.getPrice()))
                .storageLocation(request.getStorageLocation())
                .warrantyPeriod(dateFormat.parse(request.getWarrantyPeriod()))
                .maintenanceCycle(request.getMaintenanceCycle() + " tháng")
                .build();
        return deviceRepository.save(newDevice);
    }

    @Override
    public Device updateDevice(DeviceRequest request, String id) throws ParseException {
        Device deviceByDeviceIDRequest = deviceRepository.findByDeviceID(request.getDeviceID());
        if (deviceByDeviceIDRequest != null && !Objects.equals(deviceByDeviceIDRequest.getId().toString(), id)) {
            throw new IllegalArgumentException("Mã thiết bị trùng với mã của thiết bị khác. Vui lòng giữ nguyên mã thiết bị cũ hoặc chọn lại.");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Device device = deviceRepository.findById(new ObjectId(id)).orElseThrow();
        device.setDeviceID(request.getDeviceID());
        device.setDeviceName(request.getDeviceName());
        device.setSerialNumber(request.getSerialNumber());
        device.setClassification(request.getClassification());
        device.setManufacturer(request.getManufacturer());
        device.setOrigin(request.getOrigin());
        device.setManufacturingYear(Integer.parseInt(request.getManufacturingYear()));
        device.setImportationDate(dateFormat.parse(request.getImportationDate()));
        device.setPrice(Long.parseLong(request.getPrice()));
        device.setStorageLocation(request.getStorageLocation());
        device.setWarrantyPeriod(dateFormat.parse(request.getWarrantyPeriod()));
        device.setMaintenanceCycle(request.getMaintenanceCycle() + " tháng");
        deviceRepository.save(device);
        usageStatus.remove(id);
        usageStatus.put(id, calculateUsageStatus(device));
        return device;
    }

    @Override
    public Device deleteDevice(String id) {
        Device existingDevice = deviceRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
        Query queryByDevice = Query.query(Criteria.where("device.$id").is(new ObjectId(id)));
        mongoTemplate.remove(queryByDevice, Using.class);
        mongoTemplate.remove(queryByDevice, Maintenance.class);
        mongoTemplate.remove(queryByDevice, FaultRepair.class);
        deviceRepository.deleteById(new ObjectId(id));
        usageStatus.remove(id);
        return existingDevice;
    }
}
