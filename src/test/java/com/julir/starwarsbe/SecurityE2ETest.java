package com.julir.starwarsbe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julir.starwarsbe.dto.AuthResponse;
import com.julir.starwarsbe.dto.LoginRequest;
import com.julir.starwarsbe.dto.RegisterRequest;
import com.julir.starwarsbe.entity.Role;
import com.julir.starwarsbe.entity.User;
import com.julir.starwarsbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void fullAuthFlow() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user@test.com", "password123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        mockMvc.perform(get("/api/people")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest("user@test.com", "password123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deniesAccessWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/people"))
                .andExpect(status().isForbidden());
    }

    @Test
    void userCannotAccessAdminEndpoints() throws Exception {
        User user = User.builder()
                .email("user@test.com")
                .passwordHash(passwordEncoder.encode("password"))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("user@test.com", "password");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAllEndpoints() throws Exception {
        User admin = User.builder()
                .email("admin@test.com")
                .passwordHash(passwordEncoder.encode("password"))
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest("admin@test.com", "password");
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/people")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }
}
