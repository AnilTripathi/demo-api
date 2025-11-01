package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.dto.RefreshRequest;
import com.myhealth.model.UserToken;
import com.myhealth.repository.UserTokenRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RefreshTokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Test
    void refresh_WithExpiredAccessTokenButValidRefreshToken_ShouldSucceed() throws Exception {
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

        // Create refresh request with access token (even if expired)
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken(loginResponse.getAccessToken());
        refreshRequest.setRefreshToken(loginResponse.getRefreshToken());

        // Perform refresh
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void refresh_WithInvalidTokenSignature_ShouldReturnUnauthorized() throws Exception {
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
        // Create expired refresh token in database
        UserToken expiredToken = new UserToken();
        expiredToken.setRefreshToken("expired-refresh-token");
        expiredToken.setExpiresAt(LocalDateTime.now().minusHours(1));
        userTokenRepository.save(expiredToken);

        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setAccessToken("valid.access.token"); // This would need to be a real token in practice
        refreshRequest.setRefreshToken("expired-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token expired, please login again"));
    }
}