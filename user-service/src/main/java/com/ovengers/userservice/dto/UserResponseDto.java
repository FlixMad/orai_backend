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
    private String departmentId;  // 부서 ID 추가
    private String token;         // JWT 토큰 추가

    public UserResponseDto(User user) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.departmentId = user.getDepartmentId();  // 부서 ID 설정
    }

    // JWT 토큰을 포함하는 생성자 추가
    public UserResponseDto(User user, String token) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.departmentId = user.getDepartmentId();  // 부서 ID 설정
        this.token = token;  // JWT 토큰 설정
    }
}
