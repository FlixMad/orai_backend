package com.ovengers.userservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Response Estimate", description = "Responses Estimate API")
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        return "hello!!!!!!!!!!!! bye :)";
    }
}
