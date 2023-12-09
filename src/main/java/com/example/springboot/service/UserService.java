package com.example.springboot.service;

import com.example.springboot.dto.user.UserRegistrationRequestDto;
import com.example.springboot.dto.user.UserResponseDto;
import com.example.springboot.exception.RegistrationException;
import com.example.springboot.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    User findByEmail(String email);

}
