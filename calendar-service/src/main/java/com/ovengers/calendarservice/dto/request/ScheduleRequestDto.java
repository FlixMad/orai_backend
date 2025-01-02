package com.ovengers.calendarservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ScheduleRequestDto {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
}
