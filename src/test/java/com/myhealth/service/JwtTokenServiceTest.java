package com.myhealth.service;

import com.myhealth.impl.JwtTokenServiceImpl;
import com.myhealth.repository.UserTokenRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    private JwtTokenServiceImpl jwtTokenService;
    private final String testSecret = "mySecretKeyForTestingPurposesOnly123456789";
    private final long accessTokenExpiration = 900000; // 15 minutes

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenServiceImpl(userTokenRepository);
        ReflectionTestUtils.setField(jwtTokenService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpirationMs", accessTokenExpiration);
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpirationMs", 86400000L);
    }

    @Test
    void generateAccessToken_ShouldUseUserIdAsSubject() {
        // Given
        UUID userId = UUID.randomUUID();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // When
        String token = jwtTokenService.generateAccessToken(userId, authorities);

        // Then
        assertThat(token).isNotNull();
        
        Claims claims = jwtTokenService.parseClaims(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("authorities")).isEqualTo(List.of("ROLE_USER"));
    }

    @Test
    void getUserId_ShouldReturnCorrectUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String token = jwtTokenService.generateAccessToken(userId, authorities);

        // When
        UUID extractedUserId = jwtTokenService.getUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void validateTokenSignature_ShouldReturnTrueForValidSignature() {
        // Given
        UUID userId = UUID.randomUUID();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        String token = jwtTokenService.generateAccessToken(userId, authorities);

        // When
        boolean isValid = jwtTokenService.validateTokenSignature(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void parseClaimsIgnoreExpiration_ShouldParseExpiredToken() {
        // Given
        UUID userId = UUID.randomUUID();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Create token with very short expiration
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpirationMs", 1L);
        String token = jwtTokenService.generateAccessToken(userId, authorities);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        Claims claims = jwtTokenService.parseClaimsIgnoreExpiration(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
    }
}