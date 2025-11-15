package com.example.enviorapido.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

    @Test
    void generateAndValidateToken() {
        JwtUtil util = new JwtUtil("mysecretmysecretmysecretmysecret", 10_000L);
        UserDetails u = User.withUsername("bob").password("x").roles("USER").build();

        String token = util.generateToken(u);
        assertNotNull(token);

        assertEquals("bob", util.extractUsername(token));
        assertFalse(util.isTokenExpired(token));
        assertTrue(util.validateToken(token, u));
    }

    @Test
    void expiredToken_isDetected() throws InterruptedException {
        JwtUtil util = new JwtUtil("mysecretmysecretmysecretmysecret", 5L);
        UserDetails u = User.withUsername("alice").password("x").roles("USER").build();
        String token = util.generateToken(u);
        Thread.sleep(20);
        // parsing an expired token will raise ExpiredJwtException from the JJWT library
        org.junit.jupiter.api.Assertions.assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> util.isTokenExpired(token));
    }
}
