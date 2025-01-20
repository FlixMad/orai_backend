package com.ovengers.calendarservice.service;

import com.ovengers.calendarservice.dto.response.DepartmentResDto;
import com.ovengers.calendarservice.entity.Department;
import com.ovengers.calendarservice.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    // CREATE
    public Department createDepartment(Department department) {
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(department);
    }

    // READ - All Departments
    public List<DepartmentResDto> getAllDepartments() {
        return departmentRepository.findAll().stream().map(DepartmentResDto::new).collect(Collectors.toList());
    }

    // READ - Single Department
    public Department getDepartmentById(String departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    // READ - Department ID to Name Map
    public Map<String, String> getDepartmentIdNameMap() {
        return departmentRepository.findAll().stream()
                .collect(Collectors.toMap(Department::getDepartmentId, Department::getName));
    }

    // UPDATE
    public Department updateDepartment(String departmentId, Department department) {
        Department existingDepartment = getDepartmentById(departmentId);
        existingDepartment.setName(department.getName());
        existingDepartment.setType(department.getType());
        existingDepartment.setParent(department.getParent());
        existingDepartment.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(existingDepartment);
    }

    // DELETE
    public void deleteDepartment(String departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("Department not found");
        }
        departmentRepository.deleteById(departmentId);
    }
}
