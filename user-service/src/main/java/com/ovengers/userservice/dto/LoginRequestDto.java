package com.ovengers.userservice.dto;

import lombok.*;

@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;

}