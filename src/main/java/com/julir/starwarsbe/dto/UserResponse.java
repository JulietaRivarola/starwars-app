package com.julir.starwarsbe.dto;

import com.julir.starwarsbe.entity.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        Role role,
        LocalDateTime createdAt
) {}
