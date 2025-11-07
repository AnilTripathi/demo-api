package com.myhealth.dto.userprofile;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserProfileDto {

    private UUID id;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Size(max = 150)
    private String displayName;

    @Size(max = 512)
    private String profilePictureUrl;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 20)
    private String gender;

    private String bio;

    @Size(max = 255)
    private String website;

    @Size(max = 10)
    private String languagePreference;

    @Size(max = 50)
    private String timezone;

    @Size(max = 20)
    private String themePreference;

    private Boolean isPublicProfile;

    private LocalDateTime lastActiveAt;
}

