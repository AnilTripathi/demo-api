package com.myhealth.service;

import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileResponse;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;

import java.util.UUID;

public interface UserProfileService {
    
    UserProfileResponse createUserProfile(UserProfileCreateRequest request);
    
    UserProfileResponse updateUserProfile(UUID id, UserProfileUpdateRequest request);
}