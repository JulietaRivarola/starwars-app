package com.julir.starwarsbe.service;

import com.julir.starwarsbe.config.JwtUtil;
import com.julir.starwarsbe.dto.AuthResponse;
import com.julir.starwarsbe.dto.LoginRequest;
import com.julir.starwarsbe.dto.RegisterRequest;
import com.julir.starwarsbe.entity.Role;
import com.julir.starwarsbe.entity.User;
import com.julir.starwarsbe.exception.EmailAlreadyExistsException;
import com.julir.starwarsbe.exception.InvalidCredentialsException;
import com.julir.starwarsbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService service;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        String expectedToken = "jwt-token-123";
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(expectedToken);

        AuthResponse response = service.register(registerRequest);

        assertEquals(expectedToken, response.token());
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(anyString(), anyString());
    }

    @Test
    void registerThrowsExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> service.register(registerRequest));

        assertTrue(exception.getMessage().contains("Email already registered"));
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        String expectedToken = "jwt-token-123";
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(expectedToken);

        AuthResponse response = service.login(loginRequest);

        assertEquals(expectedToken, response.token());
        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPasswordHash());
        verify(jwtUtil).generateToken(anyString(), anyString());
    }

    @Test
    void loginThrowsExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> service.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void loginThrowsExceptionWhenPasswordInvalid() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> service.login(loginRequest));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), user.getPasswordHash());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
