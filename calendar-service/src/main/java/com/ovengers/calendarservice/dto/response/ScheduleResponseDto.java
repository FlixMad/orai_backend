package com.ovengers.calendarservice.dto.response;

import com.ovengers.calendarservice.entity.Schedule;
import lombok.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Setter @Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ScheduleResponseDto {

//    private String userId;

    private String ScheduleId;

    private String title;

    private String start;

    private String end;

    private String type;

    // type만 작성해줘

//    public ScheduleResponseDto(Schedule schedule) {
//        this.userId = schedule.getUserId();
//        this.title = schedule.getTitle();
//        this.start = formatDateTime(schedule.getStartTime());
//        this.end = formatDateTime(schedule.getEndTime());
//        this.type = schedule.getType().name();
//    }

    // 날짜를 ISO8601 형식의 문자열로 변환
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

}
