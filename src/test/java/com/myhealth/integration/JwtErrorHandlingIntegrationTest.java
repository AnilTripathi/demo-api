package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.ApiError;
import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.service.JwtTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtErrorHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void testInvalidCredentialsReturns400() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("nonexistent");
        authRequest.setPassword("wrongpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        assertEquals("Invalid username or password", apiError.getMessage());
        assertEquals(400, apiError.getStatus());
    }

    @Test
    void testExpiredTokenReturns401() throws Exception {
        // Create an expired token
        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 1000000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor("test-secret-key-for-testing-only-32-chars".getBytes()))
                .compact();

        MvcResult result = mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        assertEquals("Token expired", apiError.getMessage());
        assertEquals(401, apiError.getStatus());
    }

    @Test
    void testInvalidTokenReturns401() throws Exception {
        String invalidToken = "invalid.jwt.token";

        MvcResult result = mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        assertEquals("Invalid token", apiError.getMessage());
        assertEquals(401, apiError.getStatus());
    }

    @Test
    void testMalformedTokenReturns401() throws Exception {
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.malformed";

        MvcResult result = mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
        assertEquals("Invalid token", apiError.getMessage());
        assertEquals(401, apiError.getStatus());
    }

    @Test
    void testValidLoginReturnsTokens() throws Exception {
        // First register a user
        String registerJson = """
            {
                "username": "testuser",
                "password": "password123",
                "email": "test@example.com",
                "firstName": "Test",
                "lastName": "User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isCreated());

        // Then login with valid credentials
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());
        assertTrue(authResponse.getExpiresIn() > 0);
    }
}