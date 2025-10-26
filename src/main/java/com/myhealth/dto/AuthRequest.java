package com.myhealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Authentication request payload")
public class AuthRequest {
    
    @Schema(description = "Username for authentication", example = "admin", required = true)
    private String username;
    
    @Schema(description = "Password for authentication", example = "password", required = true)
    private String password;
}