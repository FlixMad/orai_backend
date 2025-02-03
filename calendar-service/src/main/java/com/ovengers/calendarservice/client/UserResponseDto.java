package com.ovengers.calendarservice.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private String email;
    private String name;
    private String profileImage;
    private String phoneNum;
    private boolean accountActive;
    private String departmentId;
    private String Position;
}
