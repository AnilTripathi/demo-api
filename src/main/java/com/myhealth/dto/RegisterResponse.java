package com.myhealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "User registration response")
public class RegisterResponse {
    
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User first name", example = "John")
    private String firstname;
    
    @Schema(description = "User last name", example = "Doe")
    private String lastname;
    
    @Schema(description = "Account enabled status", example = "true")
    private Boolean enabled;
    
    @Schema(description = "Registration timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Success message", example = "User registered successfully")
    private String message;
}