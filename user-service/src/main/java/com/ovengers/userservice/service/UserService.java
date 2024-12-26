package com.ovengers.userservice.service;

import com.ovengers.userservice.common.auth.TokenUserInfo;
import com.ovengers.userservice.dto.UserRequestDto;
import com.ovengers.userservice.dto.UserResponseDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    /**
     * 사용자 등록
     */
    public UserResponseDto createUser(UserRequestDto dto) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화 후 저장
        User user = dto.toEntity(encoder);
        User savedUser = userRepository.save(user);
        log.info("New user created: {}", savedUser.getEmail());

        // UserResponseDto로 반환
        return new UserResponseDto(savedUser);
    }

    /**
     * 로그인 처리
     */
    public UserResponseDto login(UserRequestDto dto) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("이메일을 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new UserResponseDto(user);
    }

    /**
     * 내 정보 조회
     */
    public UserResponseDto getMyInfo() {
        // 현재 인증된 사용자 정보 가져오기
        TokenUserInfo userInfo = (TokenUserInfo) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new UserResponseDto(user);
    }

    /**
     * 사용자 리스트 조회 (페이징 지원)
     */
    public List<UserResponseDto> getUserList(org.springframework.data.domain.Pageable pageable) {
        // 페이징 처리된 사용자 데이터 가져오기
        return userRepository.findAll(pageable).getContent()
                .stream()
                .map(UserResponseDto::new) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * 사용자 단건 조회
     */
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new UserResponseDto(user);
    }

    /**
     * 여러 사용자 조회 (id 리스트로 검색)
     */
    public List<UserResponseDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findByIdIn(userIds)
                .stream()
                .map(UserResponseDto::new) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }
}
