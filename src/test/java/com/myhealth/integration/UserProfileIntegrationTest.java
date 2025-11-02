package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;
import com.myhealth.entity.UserProfile;
import com.myhealth.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserProfileIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @BeforeEach
    void setUp() {
        userProfileRepository.deleteAll();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserProfile_Integration_ShouldWork() throws Exception {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setEmail("integration@test.com");
        request.setFirstName("Integration");
        request.setLastName("Test");
        request.setGender("Male");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserProfile_Integration_ShouldWork() throws Exception {
        // Create a profile first
        UserProfile profile = new UserProfile();
        profile.setEmail("update@test.com");
        profile.setFirstName("Update");
        profile.setLastName("Test");
        profile.setGender("Female");
        UserProfile saved = userProfileRepository.save(profile);
        
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("updated@test.com");
        request.setFirstName("Updated");
        request.setLastName("Test");
        request.setGender("Female");
        
        mockMvc.perform(put("/api/admin/user/{id}", saved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserProfile_DuplicateEmail_ShouldReturnConflict() throws Exception {
        // Create first profile
        UserProfile existing = new UserProfile();
        existing.setEmail("duplicate@test.com");
        existing.setFirstName("Existing");
        existing.setLastName("User");
        existing.setGender("Male");
        userProfileRepository.save(existing);
        
        // Try to create another with same email
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setEmail("duplicate@test.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setGender("Female");
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}