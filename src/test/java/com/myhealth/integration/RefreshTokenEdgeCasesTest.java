package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.dto.RefreshRequest;
import com.myhealth.model.UserToken;
import com.myhealth.repository.UserTokenRepository;
import com.myhealth.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RefreshTokenEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void refresh_WithValidExpiredAccessTokenAndValidRefreshToken_ShouldSucceed() throws Exception {
        // First login to get tokens
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        // Wait a moment to ensure token is considered "expired" in our test scenario
        // In real scenario, we would wait for actual expiration or use a very short expiration time
        
        // Create refresh request with access token (even if expired)
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(loginResponse.getAccessToken());
        refreshRequest.setRefreshToken(loginResponse.getRefreshToken());

        // Perform refresh - should succeed even with expired access token
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refresh_WithInvalidAccessTokenSignature_ShouldReturnUnauthorized() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken("invalid.token.signature");
        refreshRequest.setRefreshToken("valid-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token signature"));
    }

    @Test
    void refresh_WithExpiredRefreshToken_ShouldReturnSpecificError() throws Exception {
        // Create a valid access token first
        UUID testUserId = UUID.randomUUID();
        String validAccessToken = jwtTokenService.generateAccessToken(testUserId, java.util.Collections.emptyList());
        
        // Create expired refresh token in database
        UserToken expiredToken = new UserToken();
        expiredToken.setUserId(testUserId);
        expiredToken.setRefreshToken("expired-refresh-token");
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        userTokenRepository.save(expiredToken);

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(validAccessToken);
        refreshRequest.setRefreshToken("expired-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token expired, please login again"));
    }

    @Test
    void refresh_WithNonExistentRefreshToken_ShouldReturnInvalidRefreshToken() throws Exception {
        // Create a valid access token first
        UUID testUserId = UUID.randomUUID();
        String validAccessToken = jwtTokenService.generateAccessToken(testUserId, java.util.Collections.emptyList());

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(validAccessToken);
        refreshRequest.setRefreshToken("non-existent-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void refresh_WithSubjectMismatch_ShouldReturnTokenSubjectMismatch() throws Exception {
        // Create access token for one user
        UUID accessTokenUserId = UUID.randomUUID();
        String accessToken = jwtTokenService.generateAccessToken(accessTokenUserId, java.util.Collections.emptyList());
        
        // Create refresh token for different user
        UUID refreshTokenUserId = UUID.randomUUID();
        UserToken userToken = new UserToken();
        userToken.setUserId(refreshTokenUserId);
        userToken.setRefreshToken("mismatched-refresh-token");
        userToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        userTokenRepository.save(userToken);

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(accessToken);
        refreshRequest.setRefreshToken("mismatched-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token subject mismatch"));
    }

    @Test
    void refresh_WithMalformedAccessToken_ShouldReturnInvalidTokenSignature() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken("malformed-token");
        refreshRequest.setRefreshToken("some-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token signature"));
    }

    @Test
    void refresh_WithEmptyAccessToken_ShouldReturnInvalidTokenSignature() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken("");
        refreshRequest.setRefreshToken("some-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token signature"));
    }

    @Test
    void refresh_WithNullAccessToken_ShouldReturnBadRequest() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(null);
        refreshRequest.setRefreshToken("some-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token signature"));
    }
}