package com.ovengers.userservice.service;

import com.ovengers.userservice.common.auth.JwtTokenProvider;
import com.ovengers.userservice.common.auth.TokenUserInfo;
import com.ovengers.userservice.dto.LoginRequestDto;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import com.ovengers.userservice.common.util.MfaSecretGenerator;

import java.util.Optional;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 사용자 엔티티 생성
        User user = dto.toEntity(encoder);

        // MFA 시크릿 키 생성 및 설정
        String mfaSecret = MfaSecretGenerator.generateSecret();
        user.setUserSecret(mfaSecret);

        // 사용자 저장
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser);
    }

    public UserResponseDto login(UserRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("이메일을 찾을 수 없습니다."));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getDepartmentId());
        return new UserResponseDto(user, token);
    }

    public UserResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("이메일을 찾을 수 없습니다."));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUserId(), user.getDepartmentId());
        return new UserResponseDto(user, token);
    }

    public UserResponseDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return new UserResponseDto(user);
    }

    @Transactional
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!encoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }
    /**
     * 내 정보 조회
     */
    public UserResponseDto getMyInfo() {
        // 인증된 사용자 정보
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof TokenUserInfo)) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        TokenUserInfo userInfo = (TokenUserInfo) principal;
        User user = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new UserResponseDto(user);
    }

    /**
     * email 대신 secret으로 사용자 조회
     */
    public UserResponseDto getUserBySecret(String secret) {
        User user = userRepository.findByMfaSecret(secret)
                .orElseThrow(() -> new EntityNotFoundException("Secret으로 사용자를 찾을 수 없습니다."));

        return new UserResponseDto(user);
    }

    public String getUserSecret(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getUserSecret();
    }

    // 중복체크
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }


}

