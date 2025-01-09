package com.ovengers.calendarservice.repository;

import com.ovengers.calendarservice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
}
