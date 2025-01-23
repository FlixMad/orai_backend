package com.ovengers.calendarservice.dto.request;


import com.ovengers.calendarservice.entity.Department;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DepartmentRequestDto {
    private String name;
    private String parent;
    private String type;
}
