package com.ovengers.calendarservice.dto.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DepartmentRequestDto {
    private String name;
    private String parent;
    private String type;
}
