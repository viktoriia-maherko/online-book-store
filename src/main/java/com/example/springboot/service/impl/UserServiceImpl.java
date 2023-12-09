package com.example.springboot.service.impl;

import com.example.springboot.dto.user.UserRegistrationRequestDto;
import com.example.springboot.dto.user.UserResponseDto;
import com.example.springboot.exception.RegistrationException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.model.User;
import com.example.springboot.repository.user.UserRepository;
import com.example.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setShippingAddress(request.getShippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User with such email doesn't exist")
        );
    }
}
