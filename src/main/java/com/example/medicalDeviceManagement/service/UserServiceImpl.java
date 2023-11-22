package com.example.medicalDeviceManagement.service;

import com.example.medicalDeviceManagement.dto.ExportOperatorResponse;
import com.example.medicalDeviceManagement.dto.ListOperatorResponse;
import com.example.medicalDeviceManagement.dto.UpdateInfoOperatorRequest;
import com.example.medicalDeviceManagement.dto.UserDTO;
import com.example.medicalDeviceManagement.entity.*;
import com.example.medicalDeviceManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(new ObjectId(id)).orElseThrow();
        return UserDTO.builder()
                ._id(user.getId().toString())
                .email(user.getEmail())
                .userID(user.getUserID())
                .isAdmin(user.isAdmin())
                .name(user.getName())
                .department(user.getDepartment())
                .build();
    }

    @Override
    public String getUserNameByID(String userID) {
        User user = userRepository.findByUserID(userID);
        if (user == null) {
            return null;
        } else {
            return user.getName();
        }
    }

    @Override
    public List<String> getDepartmentsForFilter() {
        List<String> departments = mongoTemplate.query(User.class)
                .distinct("department")
                .as(String.class)
                .all();
        departments.removeIf(department -> department.equals("Administrator"));
        Collections.sort(departments);
        return departments;
    }

    @Override
    public ListOperatorResponse getListOperatorByConditions(List<String> selectedDepartment, String searchQuery, int page, int limit) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!selectedDepartment.isEmpty()) {
            criteria = criteria.and("department").in(selectedDepartment);
        }

        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("userID").regex(searchQuery, "i"),
                    Criteria.where("name").regex(searchQuery, "i"),
                    Criteria.where("email").regex(searchQuery, "i")
            );
        }
        criteria = criteria.and("isAdmin").is(false);
        query.addCriteria(criteria);

        Long totalRecords = mongoTemplate.count(query, User.class);

        query.skip((long) (page - 1) * limit);
        query.limit(limit);

        List<User> operatorList = mongoTemplate.find(query, User.class);
        List<UserDTO> responseList = operatorList.stream()
                .map(operator -> UserDTO.builder()
                        ._id(operator.getId().toString())
                        .userID(operator.getUserID())
                        .email(operator.getEmail())
                        .name(operator.getName())
                        .isAdmin(operator.isAdmin())
                        .department(operator.getDepartment())
                        .build()).toList();

        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        return ListOperatorResponse.builder()
                .list(responseList)
                .totalRecords(totalRecords)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public ExportOperatorResponse getListOperatorForExport(List<String> selectedDepartment, String searchQuery) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!selectedDepartment.isEmpty()) {
            criteria = criteria.and("department").in(selectedDepartment);
        }

        if (StringUtils.hasText(searchQuery)) {
            criteria = criteria.orOperator(
                    Criteria.where("userID").regex(searchQuery, "i"),
                    Criteria.where("name").regex(searchQuery, "i"),
                    Criteria.where("email").regex(searchQuery, "i")
            );
        }
        criteria = criteria.and("isAdmin").is(false);
        query.addCriteria(criteria);
        List<User> listOperator = mongoTemplate.find(query, User.class);
        return ExportOperatorResponse.builder()
                .list(listOperator)
                .build();
    }

    @Override
    public void updateInfoOperator(UpdateInfoOperatorRequest request, String id) {
        User operatorByRequest = userRepository.findByUserID(request.getUserID());
        if (operatorByRequest != null && !Objects.equals(operatorByRequest.getId().toString(), id)) {
            throw new IllegalArgumentException("Mã trùng với mã của người dùng khác. Vui lòng giữ nguyên mã người dùng cũ hoặc chọn lại.");
        }
        User operator = userRepository.findById(new ObjectId(id)).orElseThrow();
        operator.setUserID(request.getUserID());
        operator.setEmail(request.getEmail());
        operator.setName(request.getName());
        operator.setDepartment(request.getDepartment());
        userRepository.save(operator);
    }

    @Override
    public User deleteUser(String id) {
        User existingOperator = userRepository.findById(new ObjectId(id)).orElseThrow(() -> new RuntimeException("Không tìm thấy người vận hành này."));
        Query queryByRequester = Query.query(Criteria.where("requester.$id").is(new ObjectId(id)));
        mongoTemplate.remove(queryByRequester, UsingRequest.class);
        mongoTemplate.remove(queryByRequester, Using.class);
        mongoTemplate.remove(queryByRequester, PurchaseRequest.class);

        Query queryByReporter = Query.query(Criteria.where("reporter.$id").is(new ObjectId(id)));
        mongoTemplate.remove(queryByReporter, FaultRepair.class);
        userRepository.deleteById(new ObjectId(id));
        return existingOperator;
    }
}
