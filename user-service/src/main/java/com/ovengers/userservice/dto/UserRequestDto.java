package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
public class UserRequestDto {
    private String email;
    private String password;
    private String name;

    // User 엔티티로 변환하는 메서드
    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .email(this.email)
                .password(encoder.encode(this.password)) // 비밀번호 암호화
                .name(this.name)
//                .enabled(true) // 기본 활성화 상태 설정
                .build();
    }
}
