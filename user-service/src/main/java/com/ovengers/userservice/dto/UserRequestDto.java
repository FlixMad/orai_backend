package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String name;



}
