package com.ovengers.calendarservice.repository;

import com.ovengers.calendarservice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    @Query(value = """
        WITH RECURSIVE department_hierarchy AS (
            SELECT d.department_id, d.parent_id
            FROM tbl_department d
            WHERE d.department_id = :departmentId
            UNION ALL
            SELECT d2.department_id, d2.parent_id
            FROM tbl_department d2
            INNER JOIN department_hierarchy dh ON d2.department_id = dh.parent_id
        )
        SELECT department_id FROM department_hierarchy
        """, nativeQuery = true)
    List<String> findAllParentDepartmentIds(String departmentId);

}
