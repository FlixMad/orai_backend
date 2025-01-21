package com.ovengers.calendarservice.controller;

import com.ovengers.calendarservice.common.CommonResDto;
import com.ovengers.calendarservice.dto.response.DepartmentResDto;
import com.ovengers.calendarservice.entity.Department;
import com.ovengers.calendarservice.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // CREATE
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department createdDepartment = departmentService.createDepartment(department);
        return ResponseEntity.ok(createdDepartment);
    }

    // READ - All Departments
    @GetMapping
    public ResponseEntity<?> getAllDepartments() {
        List<DepartmentResDto> departments = departmentService.getAllDepartments();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"조회 성공",departments);
        return ResponseEntity.ok(commonResDto);
    }

    // READ - Single Department
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable("id") String departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(department);
    }

    // READ - Department ID to Name Map
    @GetMapping("/map")
    public ResponseEntity<Map<String, String>> getDepartmentIdNameMap() {
        Map<String, String> departmentMap = departmentService.getDepartmentIdNameMap();
        return ResponseEntity.ok(departmentMap);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable("id") String departmentId, @RequestBody Department department) {
        Department updatedDepartment = departmentService.updateDepartment(departmentId, department);
        return ResponseEntity.ok(updatedDepartment);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") String departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }
}
