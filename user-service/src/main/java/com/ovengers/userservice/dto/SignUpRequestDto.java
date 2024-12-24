package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.entity.UserState;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    private MultipartFile profileImage;

    private Position position;

    private String phoneNum;



    //이게 직책 컬럼이 필요한가?, 난 잘 모르겠당 일딴 보류
//    private Position position;
//    public enum Position {
//    }

    private String phoneNum;
    private boolean accountActive;
    private UserState state;
    private String affId;

    public User toEntity(PasswordEncoder encoder, String uniqueFileName) {
        return User.builder()
                .email(email)
                .password(encoder.encode(password))
                .name(name)
                .profileImage(uniqueFileName)
                .build();
    }
}
