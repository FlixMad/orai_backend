package com.ovengers.userservice.service;

import com.ovengers.userservice.dto.UserRequestDTO;
import com.ovengers.userservice.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO getUserByEmail(String email);
}
