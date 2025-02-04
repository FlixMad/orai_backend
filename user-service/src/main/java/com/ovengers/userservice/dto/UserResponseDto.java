package com.ovengers.userservice.dto;
import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import lombok.*;

import java.util.Map;

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
    private String departmentName;
    private String departmentId;

    // 기존 User 엔티티를 기반으로 하는 생성자
    public UserResponseDto(User user, Map<String,String> map) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();
        this.position = user.getPosition();
        this.phoneNum = user.getPhoneNum();
        this.accountActive = user.isAccountActive();
        this.departmentId = map.get(user.getDepartmentId());
    }
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
        this(user);  // 기존 User 엔티티 기반 생성자 호출
        this.token = token;  // JWT 토큰 설정
    }

    // 필요한 경우 추가적인 메서드나 로직을 여기에 추가할 수 있습니다.
}
