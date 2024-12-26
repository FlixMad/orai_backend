package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String email;
    private String name;

    //    private String affid;
    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
