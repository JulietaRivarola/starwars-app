package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.AuthResponse;
import com.julir.starwarsbe.dto.LoginRequest;
import com.julir.starwarsbe.dto.RegisterRequest;
import com.julir.starwarsbe.config.JwtUtil;
import com.julir.starwarsbe.entity.Role;
import com.julir.starwarsbe.entity.User;
import com.julir.starwarsbe.exception.EmailAlreadyExistsException;
import com.julir.starwarsbe.exception.InvalidCredentialsException;
import com.julir.starwarsbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException(req.email());
        }

        User user = User.builder()
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
