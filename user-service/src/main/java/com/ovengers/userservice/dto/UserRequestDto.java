package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.entity.UserState;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String name;
    private String profileImage;
    private String phoneNum;
    private Position position;
    private boolean accountActive;
    private UserState state;
    private String departmentId;

    // 추가된 mfa시크릿
    private String mfaSecret;

    // User 엔티티로 변환하는 메서드
    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .email(this.email)
                .password(encoder.encode(this.password)) // 비밀번호 암호화
                .name(this.name)
                .profileImage(this.profileImage)
                .phoneNum(this.phoneNum)
                .position(this.position)
                .accountActive(this.accountActive)
                .state(this.state)
                .departmentId(this.departmentId)
                .mfaSecret(this.mfaSecret) // 추가된 mfa 매핑
                .build();
    }
}
