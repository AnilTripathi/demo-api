package com.myhealth.impl;

import com.myhealth.constants.UserConstants;
import com.myhealth.dto.RegisterRequest;
import com.myhealth.dto.RegisterResponse;
import com.myhealth.entity.Role;
import com.myhealth.entity.User;
import com.myhealth.entity.UserProfile;
import com.myhealth.entity.UserRole;
import com.myhealth.entity.UserRoleId;
import com.myhealth.exception.ApiException;
import com.myhealth.repository.RoleRepository;
import com.myhealth.repository.UserProfileRepository;
import com.myhealth.repository.UserRepository;
import com.myhealth.repository.UserRoleRepository;
import com.myhealth.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userProfileRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ApiException("Email already registered", HttpStatus.CONFLICT);
        }
        
        // Get default role
        Role userRole = roleRepository.findByName(UserConstants.DEFAULT_USER_ROLE)
                .orElseThrow(() -> new ApiException("Default role not found", HttpStatus.INTERNAL_SERVER_ERROR));
        
        // Create user profile first (parent entity)
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(registerRequest.getEmail());
        userProfile.setFirstName(registerRequest.getFirstname());
        userProfile.setLastName(registerRequest.getLastname());
        userProfile.setDisplayName(registerRequest.getFirstname() + " " + registerRequest.getLastname());
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        
        // Create user (child entity)
        User user = new User();
        user.setId(savedProfile.getId()); // Same ID as profile
        user.setUsername(registerRequest.getEmail()); // Email as username
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(UserConstants.DEFAULT_ENABLED);
        user.setAccountNonExpired(UserConstants.DEFAULT_ACCOUNT_NON_EXPIRED);
        user.setAccountNonLocked(UserConstants.DEFAULT_ACCOUNT_NON_LOCKED);
        user.setCredentialsNonExpired(UserConstants.DEFAULT_CREDENTIALS_NON_EXPIRED);
        user.setFailedAttempts(UserConstants.DEFAULT_FAILED_ATTEMPTS);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Create user role relationship with proper composite key
        UserRoleId userRoleId = new UserRoleId(savedProfile.getId(), userRole.getId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(userRoleId);
        userRoleEntity.setUser(savedUser);
        userRoleEntity.setRole(userRole);
        
        userRoleRepository.save(userRoleEntity);
        
        return new RegisterResponse(
            savedProfile.getId(),
            savedProfile.getEmail(),
            savedProfile.getFirstName(),
            savedProfile.getLastName(),
            savedUser.getEnabled(),
            savedProfile.getCreatedAt(),
            "User registered successfully"
        );
    }
}