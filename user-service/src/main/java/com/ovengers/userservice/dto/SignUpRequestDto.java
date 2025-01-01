package com.ovengers.userservice.dto;

import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static com.ovengers.userservice.entity.UserState.IDLE;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDto {

    @Schema(description = "이메일", example = "example@example.com", required = true)
    @NotBlank @Email
    private String email;

    @Schema(description = "비밀번호", example = "abcd1234", required = true)
    @NotBlank
    private String password;

    @Schema(description = "이름", example = "John Doe", required = true)
    @NotBlank @Size(min = 2, max = 20)
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNum;

    @Schema(description = "부서 ID", example = "AFF123")
    private String departmentId;

    @Schema(description = "직급", example = "MANAGER")
    private Position position;

    @Schema(description = "프로필 이미지")
    private MultipartFile profileImage;


    public User toEntity(PasswordEncoder encoder, String uniqueFileName) {
        return User.builder()
                .email(email)
                .password(encoder.encode(password))
                .name(name)
                .profileImage(uniqueFileName)
                .accountActive(true)
                .phoneNum(phoneNum)
                .position(position)
                .state(IDLE)
                .departmentId(departmentId)
                .build();
    }
}
