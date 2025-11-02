package com.myhealth.dto.userprofile;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
public class UserProfileCreateRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;
    
    @Size(max = 150, message = "Display name must not exceed 150 characters")
    private String displayName;
    
    @URL(message = "Invalid profile picture URL")
    @Size(max = 512, message = "Profile picture URL must not exceed 512 characters")
    private String profilePictureUrl;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;
    
    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,20}$", message = "Invalid postal code format")
    private String postalCode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other|PreferNotToSay)$", message = "Gender must be Male, Female, Other, or PreferNotToSay")
    private String gender;
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    @URL(message = "Invalid website URL")
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    private String website;
    
    @Size(max = 10, message = "Language preference must not exceed 10 characters")
    private String languagePreference;
    
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;
    
    @Pattern(regexp = "^(light|dark|auto)$", message = "Theme preference must be light, dark, or auto")
    private String themePreference;
}