package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.User;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class UserRequestDto {
    private String email;
    private String password;
    private String name;



}
