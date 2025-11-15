package com.julir.starwarsbe.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "test-secret-key-minimum-256-bits-for-hs256-algorithm-security";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(TEST_SECRET, 1);
    }

    @Test
    void generatesValidToken() {
        String token = jwtUtil.generateToken("user@test.com", "USER");

        assertNotNull(token);
        assertEquals("user@test.com", jwtUtil.getEmail(token));
        assertEquals("USER", jwtUtil.getRole(token));
    }

    @Test
    void extractsClaimsCorrectly() {
        String token = jwtUtil.generateToken("admin@test.com", "ADMIN");

        assertEquals("admin@test.com", jwtUtil.getEmail(token));
        assertEquals("ADMIN", jwtUtil.getRole(token));
    }

    @Test
    void rejectsInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.getEmail("invalid.token"));
    }

    @Test
    void rejectsExpiredToken() {
        Date now = new Date();
        String expiredToken = Jwts.builder()
                .subject("expired@test.com")
                .issuedAt(new Date(now.getTime() - 2000))
                .expiration(new Date(now.getTime() - 1000))
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.getEmail(expiredToken));
    }
}
