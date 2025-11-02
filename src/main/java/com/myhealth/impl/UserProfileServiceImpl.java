package com.myhealth.impl;

import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileResponse;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;
import com.myhealth.entity.UserProfile;
import com.myhealth.exception.DuplicateResourceException;
import com.myhealth.exception.ResourceNotFoundException;
import com.myhealth.mapper.UserProfileMapper;
import com.myhealth.repository.UserProfileRepository;
import com.myhealth.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    
    @Override
    public UserProfileResponse createUserProfile(UserProfileCreateRequest request) {
        if (userProfileRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }
        
        UserProfile userProfile = userProfileMapper.toEntity(request);
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toResponse(savedProfile);
    }
    
    @Override
    public UserProfileResponse updateUserProfile(UUID id, UserProfileUpdateRequest request) {
        UserProfile existingProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));
        
        if (userProfileRepository.existsByEmailAndIdNot(request.getEmail().trim(), id)) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }
        
        userProfileMapper.updateEntity(request, existingProfile);
        UserProfile savedProfile = userProfileRepository.save(existingProfile);
        return userProfileMapper.toResponse(savedProfile);
    }
}