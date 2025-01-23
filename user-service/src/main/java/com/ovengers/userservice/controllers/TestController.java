package com.ovengers.userservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;

@RestController
@RequiredArgsConstructor
@Tag(name = "Response Estimate", description = "Responses Estimate API")
public class TestController {

    private final Environment env;

    @GetMapping("/hello")
    public String hello() {
        return "hello!!!!!!!!!!!! bye :)";
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", gateway ip=" + env.getProperty("gateway.ip")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration_time"));
    }
}
