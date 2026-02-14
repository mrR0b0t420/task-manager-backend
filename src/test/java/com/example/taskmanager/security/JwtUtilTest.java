package com.example.taskmanager.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void testGenerateTokenAndValidate() {
        UserDetails userDetails = new User("testuser", "password", java.util.Collections
                .singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("USER")));
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, userDetails));
        assertEquals("testuser", jwtUtil.extractUsername(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testExtractUsername() {
        UserDetails userDetails = new User("testuser", "password", java.util.Collections
                .singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("USER")));
        String token = jwtUtil.generateToken(userDetails);
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }
}
