package com.myhealth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.RegisterRequest;
import com.myhealth.dto.RegisterResponse;
import com.myhealth.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class UserRegistrationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRegistrationService userRegistrationService;
    
    @MockBean
    private com.myhealth.service.AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testSuccessfulRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("password123");
        
        RegisterResponse response = new RegisterResponse(
            UUID.randomUUID(),
            "test@example.com",
            "John",
            "Doe",
            true,
            LocalDateTime.now(),
            "User registered successfully"
        );
        
        when(userRegistrationService.registerUser(any(RegisterRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
    
    @Test
    public void testRegistrationWithInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testRegistrationWithMissingFields() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        // Missing firstname, lastname, password
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testRegistrationWithShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("123"); // Too short
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}