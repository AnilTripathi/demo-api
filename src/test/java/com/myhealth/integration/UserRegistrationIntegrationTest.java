package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.RegisterRequest;
import com.myhealth.entity.Role;
import com.myhealth.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserRegistrationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @BeforeEach
    public void setUp() {
        // Ensure ROLE_USER exists
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("Standard user role");
            roleRepository.save(userRole);
        }
    }
    
    @Test
    public void testSuccessfulUserRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("integration@example.com");
        request.setFirstname("Integration");
        request.setLastname("Test");
        request.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.firstname").value("Integration"))
                .andExpect(jsonPath("$.lastname").value("Test"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
    
    @Test
    public void testDuplicateEmailRegistration() throws Exception {
        RegisterRequest request1 = new RegisterRequest();
        request1.setEmail("duplicate@example.com");
        request1.setFirstname("First");
        request1.setLastname("User");
        request1.setPassword("password123");
        
        // First registration should succeed
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());
        
        RegisterRequest request2 = new RegisterRequest();
        request2.setEmail("duplicate@example.com"); // Same email
        request2.setFirstname("Second");
        request2.setLastname("User");
        request2.setPassword("password456");
        
        // Second registration should fail
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }
    
    @Test
    public void testRegistrationWithInvalidData() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email"); // Invalid email format
        request.setFirstname(""); // Empty firstname
        request.setLastname("Test");
        request.setPassword("123"); // Too short password
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}