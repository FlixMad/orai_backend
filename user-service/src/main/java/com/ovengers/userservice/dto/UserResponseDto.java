package com.ovengers.userservice.dto;
import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String userId;
    private String token;
    private String email;
    private String name;
    private String profileImage;
    private Position position;
    private String phoneNum;
    private boolean accountActive;
    private String departmentId;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();
        this.position = user.getPosition();
        this.phoneNum = user.getPhoneNum();
        this.accountActive = user.isAccountActive();
        this.departmentId = user.getDepartmentId();
    }
    // JWT 토큰을 포함하는 생성자 추가
    public UserResponseDto(User user, String token) {
        this(user);  // 기존 User 엔티티 기반 생성자를 호출
        this.token = token;  // JWT 토큰 설정
    }

}
