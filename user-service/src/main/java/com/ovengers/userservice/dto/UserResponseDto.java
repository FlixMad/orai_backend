package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;           // userId로 이름 변경 (User 엔티티에서 가져오기)
    private String email;
    private String name;
    private String departmentId;     // 부서 ID 추가
    private String token;            // JWT 토큰 추가

    // User 엔티티를 받아서 변환하는 생성자
    public UserResponseDto(User user) {
        this.userId = user.getUserId();  // userId로 이름 변경
        this.email = user.getEmail();
        this.name = user.getName();
        this.departmentId = user.getDepartmentId();  // 부서 ID 설정
    }

    // JWT 토큰을 포함하는 생성자 추가
    public UserResponseDto(User user, String token) {
        this(user);  // 기존 User 엔티티 기반 생성자를 호출
        this.token = token;  // JWT 토큰 설정
    }
}
