package com.ovengers.calendarservice.dto.response;

import com.ovengers.calendarservice.entity.Department;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResDto {

    private String departmentId; // 부서 아이디

    private String departmentName; // 부서 이름

    private String type; // 부서 유형 (ENUM -> String)

    private String parentId; // 상위 부서 ID

    public DepartmentResDto(Department department) {
        this.departmentId = department.getDepartmentId();
        this.departmentName = department.getName();
        this.type = String.valueOf(department.getType());
        this.parentId = department.getParent() == null ? "" : department.getParent().getDepartmentId();
    }

}
