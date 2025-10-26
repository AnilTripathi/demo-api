package com.myhealth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.AuthRequest;
import com.myhealth.entity.Role;
import com.myhealth.entity.User;
import com.myhealth.entity.UserProfile;
import com.myhealth.entity.UserRole;
import com.myhealth.entity.UserRoleId;
import com.myhealth.repository.RoleRepository;
import com.myhealth.repository.UserProfileRepository;
import com.myhealth.repository.UserRepository;
import com.myhealth.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProjectionAuthenticationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    public void setUp() {
        // Create role
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("Standard user role");
        Role savedRole = roleRepository.save(userRole);
        
        // Create user profile
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("auth@example.com");
        userProfile.setFirstName("Auth");
        userProfile.setLastName("User");
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        
        // Create user
        User user = new User();
        user.setId(savedProfile.getId());
        user.setUsername("auth@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setFailedAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        // Create user role relationship
        UserRoleId userRoleId = new UserRoleId(savedUser.getId(), savedRole.getId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(userRoleId);
        userRoleEntity.setUser(savedUser);
        userRoleEntity.setRole(savedRole);
        userRoleRepository.save(userRoleEntity);
    }
    
    @Test
    public void testLoginWithProjection_Success() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("auth@example.com");
        authRequest.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }
    
    @Test
    public void testLoginWithInvalidCredentials_Failure() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("auth@example.com");
        authRequest.setPassword("wrongpassword");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testLoginWithNonExistentUser_Failure() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("nonexistent@example.com");
        authRequest.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testLoginWithDisabledUser_Failure() throws Exception {
        // Create disabled user
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
        
        UserProfile disabledProfile = new UserProfile();
        disabledProfile.setEmail("disabled@example.com");
        disabledProfile.setFirstName("Disabled");
        disabledProfile.setLastName("User");
        disabledProfile.setCreatedAt(LocalDateTime.now());
        disabledProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedDisabledProfile = userProfileRepository.save(disabledProfile);
        
        User disabledUser = new User();
        disabledUser.setId(savedDisabledProfile.getId());
        disabledUser.setUsername("disabled@example.com");
        disabledUser.setPassword(passwordEncoder.encode("password123"));
        disabledUser.setEnabled(false); // Disabled user
        disabledUser.setAccountNonExpired(true);
        disabledUser.setAccountNonLocked(true);
        disabledUser.setCredentialsNonExpired(true);
        disabledUser.setFailedAttempts(0);
        disabledUser.setCreatedAt(LocalDateTime.now());
        disabledUser.setUpdatedAt(LocalDateTime.now());
        User savedDisabledUser = userRepository.save(disabledUser);
        
        UserRoleId userRoleId = new UserRoleId(savedDisabledUser.getId(), userRole.getId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(userRoleId);
        userRoleEntity.setUser(savedDisabledUser);
        userRoleEntity.setRole(userRole);
        userRoleRepository.save(userRoleEntity);
        
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("disabled@example.com");
        authRequest.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}