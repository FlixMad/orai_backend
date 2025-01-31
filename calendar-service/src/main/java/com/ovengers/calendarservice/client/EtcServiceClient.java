package com.ovengers.calendarservice.client;

import com.ovengers.calendarservice.dto.NotificationEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "etc-service", url = "http://etc-service.default.svc.cluster.local:8082")
public interface EtcServiceClient {
    @PostMapping("api/notifications")
    ResponseEntity<?> createNotification(@RequestBody NotificationEvent event);
}
