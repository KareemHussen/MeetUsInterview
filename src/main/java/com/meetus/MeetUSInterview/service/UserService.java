package com.meetus.MeetUSInterview.service;

import com.meetus.MeetUSInterview.dto.request.auth.LoginRequest;
import com.meetus.MeetUSInterview.dto.request.auth.RegisterRequest;
import com.meetus.MeetUSInterview.dto.response.auth.AuthResponse;
import com.meetus.MeetUSInterview.dto.response.auth.UserResponse;
import com.meetus.MeetUSInterview.entity.User;
import com.meetus.MeetUSInterview.mapper.UserMapper;
import com.meetus.MeetUSInterview.repository.UserRepository;
import com.meetus.MeetUSInterview.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        String token = jwtUtil.generateToken(savedUser);

        UserResponse userResponse = userMapper.toResponse(savedUser);
        return AuthResponse.of(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getId(), request.getPassword())
        );

        log.info("User logged in successfully: {}", user.getEmail());
        String token = jwtUtil.generateToken(user);

        UserResponse userResponse = userMapper.toResponse(user);
        return AuthResponse.of(token, userResponse);
    }
}
