package com.ovengers.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "calendar-service", url = "http://calendar-service.default.svc.cluster.local:8084")
public interface CalendarServiceClient {
    @GetMapping("/api/departments/map")
    Map<String, String> getDepartmentMap();
}
