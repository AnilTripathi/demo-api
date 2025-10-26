package com.myhealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Refresh token request payload")
public class RefreshRequest {
    
    @Schema(description = "Refresh token to generate new access token", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private String refreshToken;
}