
package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.SignUpRequestDto;
import com.ovengers.userservice.entity.Position;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SignUpRequestDto signUpRequestDto;

    @BeforeEach
    void setUp() {
        signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password123")
                .phoneNum("010-1234-5678")
                .departmentId("AFF123")
                .position(Position.MANAGER)
                .mfaSecret("test-secret")
                .build();
    }

    @Test
    void 유저_생성_성공() {
        // Given
        String uniqueFileName = "profile.png";
        User mockUser = new User();
        mockUser.setUserId("12345");
        given(userRepository.save(any(User.class))).willReturn(mockUser);
        given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");

        // When
        User createdUser = adminService.createUser(signUpRequestDto, uniqueFileName);
        logger.info("유저 생성 성공: {}", createdUser);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUserId()).isEqualTo("12345");

        then(userRepository).should(times(1)).save(any(User.class));
    }

    @Test
    void 유저_삭제_성공() {
        // Given
        String userId = "12345";
        User mockUser = new User();
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // When
        boolean isDeleted = adminService.deleteUser(userId);
        logger.info("유저 삭제 성공: {}", userId);

        // Then
        assertThat(isDeleted).isTrue();
        then(userRepository).should(times(1)).delete(mockUser);
    }

    @Test
    void 유저_삭제_실패_존재하지않음() {
        // Given
        String userId = "12345";
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> adminService.deleteUser(userId));
        logger.error("유저 삭제 실패: {}", exception.getMessage());

        assertThat(exception.getMessage()).isEqualTo("User not found.");
    }
}
