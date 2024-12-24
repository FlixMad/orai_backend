package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.SignUpRequestDto;
import com.ovengers.userservice.entity.User;
import com.ovengers.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService{
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User createUser(@Valid SignUpRequestDto dto, String uniqueFileName) {
        return userRepository.save(dto.toEntity(encoder,uniqueFileName));
    }
}
