package com.ovengers.userservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.dto.LoginRequestDto;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.UserRepository;
import com.ovengers.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setDepartmentId("dept-123");
    }

    @Test
    void testCreateUser() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        dto.setDepartmentId("dept-123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(encoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto response = userService.createUser(dto);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(dto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(user.getUserId(), user.getEmail(), user.getDepartmentId())).thenReturn("mockToken");

        UserResponseDto response = userService.login(dto);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockToken");
    }

    @Test
    void testGetUserById() {
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(user.getUserId());

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
    }
}
