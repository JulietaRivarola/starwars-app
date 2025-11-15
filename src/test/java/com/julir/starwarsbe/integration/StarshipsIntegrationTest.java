package com.julir.starwarsbe.integration;

import com.julir.starwarsbe.config.JwtUtil;
import com.julir.starwarsbe.entity.Role;
import com.julir.starwarsbe.entity.User;
import com.julir.starwarsbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StarshipsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@test.com")
                .passwordHash(passwordEncoder.encode("password"))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        token = jwtUtil.generateToken("test@test.com", "USER");
    }

    @Test
    void shouldListStarships() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void filtersStarshipsByName() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .param("name", "wing")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void filtersStarshipsByModel() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .param("model", "T-65")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPaginateStarships() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .param("page", "2")
                        .param("limit", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").isNumber());
    }

    @Test
    void returnsExpandedStarshipInfo() throws Exception {
        mockMvc.perform(get("/api/starships")
                        .param("expanded", "true")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].properties").exists());
    }
}
