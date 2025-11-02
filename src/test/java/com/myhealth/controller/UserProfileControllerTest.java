package com.myhealth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileResponse;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;
import com.myhealth.exception.DuplicateResourceException;
import com.myhealth.exception.ResourceNotFoundException;
import com.myhealth.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserProfileService userProfileService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserProfile_ShouldReturnCreated() throws Exception {
        UserProfileCreateRequest request = createValidCreateRequest();
        UserProfileResponse response = createValidResponse();
        
        when(userProfileService.createUserProfile(any(UserProfileCreateRequest.class)))
                .thenReturn(response);
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserProfile_WithDuplicateEmail_ShouldReturnConflict() throws Exception {
        UserProfileCreateRequest request = createValidCreateRequest();
        
        when(userProfileService.createUserProfile(any(UserProfileCreateRequest.class)))
                .thenThrow(new DuplicateResourceException("Email already exists"));
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserProfile_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        // Missing required fields
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserProfile_ShouldReturnOk() throws Exception {
        UUID profileId = UUID.randomUUID();
        UserProfileUpdateRequest request = createValidUpdateRequest();
        UserProfileResponse response = createValidResponse();
        
        when(userProfileService.updateUserProfile(eq(profileId), any(UserProfileUpdateRequest.class)))
                .thenReturn(response);
        
        mockMvc.perform(put("/api/admin/user/{id}", profileId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserProfile_WithNotFound_ShouldReturnNotFound() throws Exception {
        UUID profileId = UUID.randomUUID();
        UserProfileUpdateRequest request = createValidUpdateRequest();
        
        when(userProfileService.updateUserProfile(eq(profileId), any(UserProfileUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("User profile not found"));
        
        mockMvc.perform(put("/api/admin/user/{id}", profileId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void createUserProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        UserProfileCreateRequest request = createValidCreateRequest();
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createUserProfile_WithUserRole_ShouldReturnForbidden() throws Exception {
        UserProfileCreateRequest request = createValidCreateRequest();
        
        mockMvc.perform(post("/api/admin/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    private UserProfileCreateRequest createValidCreateRequest() {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setEmail("john.doe@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGender("Male");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        return request;
    }
    
    private UserProfileUpdateRequest createValidUpdateRequest() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("john.doe@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGender("Male");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        return request;
    }
    
    private UserProfileResponse createValidResponse() {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(UUID.randomUUID());
        response.setEmail("john.doe@example.com");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setGender("Male");
        response.setDateOfBirth(LocalDate.of(1990, 5, 15));
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
        return response;
    }
}