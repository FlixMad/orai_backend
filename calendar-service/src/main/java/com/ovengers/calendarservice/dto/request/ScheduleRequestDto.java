package com.ovengers.calendarservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ovengers.calendarservice.entity.Schedule.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequestDto {
    private String title;

    private String description;

    private String departmentId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    private ScheduleStatus scheduleStatus;
}
