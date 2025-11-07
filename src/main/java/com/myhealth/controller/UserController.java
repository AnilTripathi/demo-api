package com.myhealth.controller;

import com.myhealth.dto.UserInfo;
import com.myhealth.dto.userprofile.UserProfileDto;
import com.myhealth.dto.userprofile.UserProfileRequest;
import com.myhealth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {
    
    private final UserService userService;
    
    @Operation(
        summary = "Get all users", 
        description = "Retrieve a list of all users with their basic information",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = UserInfo.class))
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = com.myhealth.dto.ApiError.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> me() {
        UserProfileDto userProfile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("profile/update")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(@RequestBody @Valid UserProfileRequest request) {
        UserProfileDto userProfile = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(userProfile);
    }
}