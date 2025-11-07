package com.myhealth.impl;

import com.myhealth.dto.UserInfo;
import com.myhealth.dto.userprofile.UserProfileDto;
import com.myhealth.dto.userprofile.UserProfileRequest;
import com.myhealth.entity.UserProfile;
import com.myhealth.exception.ResourceNotFoundException;
import com.myhealth.repository.UserProfileRepository;
import com.myhealth.service.JwtTokenService;
import com.myhealth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserProfileRepository userProfileRepository;
    private final JwtTokenService jwtTokenService;
    
    @Override
    public List<UserInfo> getAllUsers() {
        return userProfileRepository.findAll()
                .stream()
                .map(this::mapToUserInfo)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserProfileDto getCurrentUserProfile() {
        UUID userId = jwtTokenService.getLoggedInUserId();
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        return mapToUserProfileDto(userProfile);
    }
    
    @Override
    public UserProfileDto updateCurrentUserProfile(UserProfileRequest request) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        
        updateUserProfileFromRequest(request, userProfile);
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return mapToUserProfileDto(savedProfile);
    }
    
    private UserInfo mapToUserInfo(UserProfile userProfile) {
        return new UserInfo(
                userProfile.getId(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getEmail(),
                userProfile.getProfilePictureUrl()
        );
    }
    
    private UserProfileDto mapToUserProfileDto(UserProfile userProfile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(userProfile.getId());
        dto.setEmail(userProfile.getEmail());
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setDisplayName(userProfile.getDisplayName());
        dto.setProfilePictureUrl(userProfile.getProfilePictureUrl());
        dto.setPhoneNumber(userProfile.getPhoneNumber());
        dto.setAddressLine1(userProfile.getAddressLine1());
        dto.setAddressLine2(userProfile.getAddressLine2());
        dto.setCity(userProfile.getCity());
        dto.setState(userProfile.getState());
        dto.setPostalCode(userProfile.getPostalCode());
        dto.setCountry(userProfile.getCountry());
        dto.setDateOfBirth(userProfile.getDateOfBirth());
        dto.setGender(userProfile.getGender());
        dto.setBio(userProfile.getBio());
        dto.setWebsite(userProfile.getWebsite());
        dto.setLanguagePreference(userProfile.getLanguagePreference());
        dto.setTimezone(userProfile.getTimezone());
        dto.setThemePreference(userProfile.getThemePreference());
        dto.setIsPublicProfile(userProfile.getIsPublicProfile());
        dto.setLastActiveAt(userProfile.getLastActiveAt());
        return dto;
    }
    
    private void updateUserProfileFromRequest(UserProfileRequest request, UserProfile userProfile) {
        userProfile.setEmail(request.getEmail().trim());
        userProfile.setFirstName(request.getFirstName().trim());
        userProfile.setLastName(request.getLastName().trim());
        userProfile.setDisplayName(trimOrNull(request.getDisplayName()));
        userProfile.setProfilePictureUrl(trimOrNull(request.getProfilePictureUrl()));
        userProfile.setPhoneNumber(trimOrNull(request.getPhoneNumber()));
        userProfile.setAddressLine1(trimOrNull(request.getAddressLine1()));
        userProfile.setAddressLine2(trimOrNull(request.getAddressLine2()));
        userProfile.setCity(trimOrNull(request.getCity()));
        userProfile.setState(trimOrNull(request.getState()));
        userProfile.setPostalCode(trimOrNull(request.getPostalCode()));
        userProfile.setCountry(trimOrNull(request.getCountry()));
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setGender(request.getGender());
        userProfile.setBio(trimOrNull(request.getBio()));
        userProfile.setWebsite(trimOrNull(request.getWebsite()));
        userProfile.setLanguagePreference(trimOrNull(request.getLanguagePreference()));
        userProfile.setTimezone(trimOrNull(request.getTimezone()));
        userProfile.setThemePreference(trimOrNull(request.getThemePreference()));
        userProfile.setIsPublicProfile(request.getIsPublicProfile());
        userProfile.setLastActiveAt(request.getLastActiveAt());
    }
    
    private String trimOrNull(String value) {
        return value != null ? value.trim() : null;
    }
}