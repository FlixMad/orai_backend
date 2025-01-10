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
public class UserUpdateDto {

        private String email;
        private String name;
        private String profileImage;
        private String phoneNum;
        private Position position;  // 추가된 필드
        private String departmentId;

        // User 엔티티로 변환하는 메서드
        public User toEntity(PasswordEncoder encoder) {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .profileImage(this.profileImage)
                    .phoneNum(this.phoneNum)  // 추가된 필드
                    .position(this.position)  // 추가된 필드
                    .departmentId(this.departmentId)  // 추가된 필드
                    .build();
        }

}