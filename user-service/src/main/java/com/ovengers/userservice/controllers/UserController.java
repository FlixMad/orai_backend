package com.ovengers.userservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "UserController", description = "인사팀이 아닌 유저 api controller")
public class UserController {
}
