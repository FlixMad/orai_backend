package com.ovengers.calendarservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class NotificationEvent {
    private List<String> userIds;
    private NotificationMessage message;

    @JsonCreator
    public NotificationEvent(
            @JsonProperty("userIds") List<String> userIds,
            @JsonProperty("message") NotificationMessage message
    ) {
        this.userIds = userIds;
        this.message = message;
    }
}
