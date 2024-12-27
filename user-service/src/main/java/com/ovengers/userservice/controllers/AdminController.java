package com.ovengers.userservice.controllers;
import com.ovengers.userservice.common.configs.AwsS3Config;
import com.ovengers.userservice.common.dto.CommonResDto;
import com.ovengers.userservice.dto.SignUpRequestDto;
import com.ovengers.userservice.dto.UserResponseDTO;
import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;
    private final AwsS3Config s3Config;

    @Operation(summary = "사용자 조회(리스트)", description = "사용자 조회할 때 사용하는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping(value = "/admin/users")
    public ResponseEntity<CommonResDto> getUsers(@RequestParam Map<String, String> params) {
        log.info("Search params: {}", params);
        List<UserResponseDTO> users = adminService.search(params);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 성공", users);
        return ResponseEntity.ok(commonResDto);
    }


    @Operation(summary = "사용자 조회(페이지)(오버라이딩)", description = "사용자 조회할 때 사용하는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PageableAsQueryParam
    @GetMapping(value = "/admin/users-page")
    public ResponseEntity<?> getUsers(@RequestParam Map<String,String> params,
                                      @PageableDefault(size = 10, page = 0) Pageable pageable) {
        log.info("params : {}", params);
        List<UserResponseDTO> users = adminService.search(params);
        Page<UserResponseDTO> userPage = adminService.listToPage(users,pageable);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"조회 성공", userPage);
        return ResponseEntity.status(HttpStatus.OK).body(commonResDto);
    }
    @Operation(summary = "사용자 생성", description = "관리자가 사용자 생성할 때 사용하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping(value = "/admin/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(
            @Parameter(description = "이메일", example = "example@example.com", required = true) @RequestParam String email,
            @Parameter(description = "비밀번호", example = "abcd1234", required = true) @RequestParam String password,
            @Parameter(description = "이름", example = "John Doe", required = true) @RequestParam String name,
            @Parameter(description = "전화번호", example = "010-1234-5678") @RequestParam(required = false) String phoneNum,
            @Parameter(description = "부서 ID", example = "AFF123") @RequestParam(required = false) String departmentId,
            @Parameter(description = "직급", example = "MANAGER") @RequestParam(required = false) Position position,
            @Parameter(description = "프로필 이미지 파일") @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        // DTO 생성
        SignUpRequestDto dto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNum(phoneNum)
                .departmentId(departmentId)
                .position(position)
                .build();

        log.info("user-service/admins/users: POST, dto: {}", dto);

        // 파일 처리 로직
        String uniqueFileName;
        if (profileImage != null && !profileImage.isEmpty()) {
            uniqueFileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
            // S3 파일 업로드 로직 추가 (생략)
            String imageUrl
                    = s3Config.uploadToS3Bucket(profileImage.getBytes(), uniqueFileName);
            // 사용자 생성
            User user = adminService.createUser(dto, imageUrl);
            // 성공 응답
            CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "회원가입 성공", user.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(resDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
}
