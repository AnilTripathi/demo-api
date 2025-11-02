package com.myhealth.controller;

import com.myhealth.dto.userprofile.UserProfileCreateRequest;
import com.myhealth.dto.userprofile.UserProfileResponse;
import com.myhealth.dto.userprofile.UserProfileUpdateRequest;
import com.myhealth.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Admin endpoints for managing user profiles")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @PostMapping
    @Operation(summary = "Create user profile", description = "Create a new user profile (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserProfileResponse> createUserProfile(@Valid @RequestBody UserProfileCreateRequest request) {
        UserProfileResponse response = userProfileService.createUserProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Update an existing user profile by ID (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
        @ApiResponse(responseCode = "404", description = "User profile not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse response = userProfileService.updateUserProfile(id, request);
        return ResponseEntity.ok(response);
    }
}