package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String userId;
    private String email;
    private String name;
    private String profileImage;
    private Position position;
    private String phoneNum;
    private boolean accountActive;
    private String departmentId;

    public UserResponseDTO(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();
        this.position = user.getPosition();
        this.phoneNum = user.getPhoneNum();
        this.accountActive = user.isAccountActive();
        this.departmentId = user.getDepartmentId();
    }

}
