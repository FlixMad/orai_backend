package com.ovengers.userservice.controllers;

import com.ovengers.userservice.common.dto.CommonResDto;
import com.ovengers.userservice.dto.SignUpRequestDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "AdminController", description = "인사팀 유저 api controller")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "업체 회원가입", description = "업체 측에서 회원가입 할 때 사용하는 API")
    @Parameters({
            @Parameter(name = "email", description = "이메일", example = "chrome123@naver.com"),
            @Parameter(name = "password", description = "6자~12자 이내", example = "abcd1234"),
            @Parameter(name = "name", description = "2자 ~ 20장 이내", example = "이마루"),
            @Parameter(name = "profileImage", description = "프로필 이미지")
            //프로필 하는중
    })
    @PostMapping(value = "/admins/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid SignUpRequestDto dto){
        log.info("user-service/admins/user: Post , dto: {}", dto.toString());

        MultipartFile profileImage = dto.getProfileImage();

        String uniqueFileName
                = UUID.randomUUID() + "_" +     profileImage.getOriginalFilename();
        // s3에 파일 넣는 로직 추가하기. s3 아직 없어서 생략

        User user = adminService.createUser(dto, uniqueFileName);

        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "user create 성공", user.getUserId());
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
