package com.myhealth.mapper;

import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileResponse;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;
import com.myhealth.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    
    public UserProfile toEntity(UserProfileCreateRequest request) {
        UserProfile profile = new UserProfile();
        profile.setEmail(request.getEmail().trim());
        profile.setFirstName(request.getFirstName().trim());
        profile.setLastName(request.getLastName().trim());
        profile.setDisplayName(trimOrNull(request.getDisplayName()));
        profile.setProfilePictureUrl(trimOrNull(request.getProfilePictureUrl()));
        profile.setPhoneNumber(trimOrNull(request.getPhoneNumber()));
        profile.setAddressLine1(trimOrNull(request.getAddressLine1()));
        profile.setAddressLine2(trimOrNull(request.getAddressLine2()));
        profile.setCity(trimOrNull(request.getCity()));
        profile.setState(trimOrNull(request.getState()));
        profile.setPostalCode(trimOrNull(request.getPostalCode()));
        profile.setCountry(trimOrNull(request.getCountry()));
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(trimOrNull(request.getBio()));
        profile.setWebsite(trimOrNull(request.getWebsite()));
        profile.setLanguagePreference(trimOrNull(request.getLanguagePreference()));
        profile.setTimezone(trimOrNull(request.getTimezone()));
        profile.setThemePreference(trimOrNull(request.getThemePreference()));
        return profile;
    }
    
    public void updateEntity(UserProfileUpdateRequest request, UserProfile profile) {
        profile.setEmail(request.getEmail().trim());
        profile.setFirstName(request.getFirstName().trim());
        profile.setLastName(request.getLastName().trim());
        profile.setDisplayName(trimOrNull(request.getDisplayName()));
        profile.setProfilePictureUrl(trimOrNull(request.getProfilePictureUrl()));
        profile.setPhoneNumber(trimOrNull(request.getPhoneNumber()));
        profile.setAddressLine1(trimOrNull(request.getAddressLine1()));
        profile.setAddressLine2(trimOrNull(request.getAddressLine2()));
        profile.setCity(trimOrNull(request.getCity()));
        profile.setState(trimOrNull(request.getState()));
        profile.setPostalCode(trimOrNull(request.getPostalCode()));
        profile.setCountry(trimOrNull(request.getCountry()));
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(trimOrNull(request.getBio()));
        profile.setWebsite(trimOrNull(request.getWebsite()));
        profile.setLanguagePreference(trimOrNull(request.getLanguagePreference()));
        profile.setTimezone(trimOrNull(request.getTimezone()));
        profile.setThemePreference(trimOrNull(request.getThemePreference()));
    }
    
    public UserProfileResponse toResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setEmail(profile.getEmail());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setDisplayName(profile.getDisplayName());
        response.setProfilePictureUrl(profile.getProfilePictureUrl());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setAddressLine1(profile.getAddressLine1());
        response.setAddressLine2(profile.getAddressLine2());
        response.setCity(profile.getCity());
        response.setState(profile.getState());
        response.setPostalCode(profile.getPostalCode());
        response.setCountry(profile.getCountry());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setGender(profile.getGender());
        response.setBio(profile.getBio());
        response.setWebsite(profile.getWebsite());
        response.setLanguagePreference(profile.getLanguagePreference());
        response.setTimezone(profile.getTimezone());
        response.setThemePreference(profile.getThemePreference());
        response.setIsPublicProfile(profile.getIsPublicProfile());
        response.setLastActiveAt(profile.getLastActiveAt());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
    
    private String trimOrNull(String value) {
        return value != null ? value.trim() : null;
    }
}