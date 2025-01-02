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
@NoArgsConstructor // 기본 생성자 추가
public class UserRequestDto {
    private String email;
    private String password;
    private String name;
    private String profileImage;
    private String phoneNum;
    private Position position;  // 추가된 필드
    private boolean accountActive;
    private UserState state;
    private String departmentId;

    // User 엔티티로 변환하는 메서드
    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .email(this.email)
                .password(encoder.encode(this.password)) // 비밀번호 암호화
                .name(this.name)
                .profileImage(this.profileImage)
                .phoneNum(this.phoneNum)  // 추가된 필드
                .position(this.position)  // 추가된 필드
                .accountActive(this.accountActive)
                .state(this.state)
                .departmentId(this.departmentId)  // 추가된 필드
                .build();
    }

}
