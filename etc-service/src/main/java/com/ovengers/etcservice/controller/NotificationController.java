package com.ovengers.etcservice.controller;

import com.ovengers.etcservice.common.dto.CommonResDto;
import com.ovengers.etcservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public CommonResDto<?> getNotification() {
        return null;
    }

    @PostMapping
    public CommonResDto<?> createNotification(){
        return null;
    }

}
