package com.myhealth.dto.userprofile;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserProfileResponse {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private String profilePictureUrl;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDate dateOfBirth;
    private String gender;
    private String bio;
    private String website;
    private String languagePreference;
    private String timezone;
    private String themePreference;
    private Boolean isPublicProfile;
    private LocalDateTime lastActiveAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}