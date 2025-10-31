package com.myhealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Simple error response")
public class ErrorResponse {
    
    @Schema(description = "Error message", example = "Invalid username or password")
    private String message;
}