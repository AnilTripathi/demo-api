package com.myhealth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "API error response")
public class ApiError {
    
    @Schema(description = "Error timestamp", example = "2024-01-01T12:00:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP status code", example = "401")
    private int status;
    
    @Schema(description = "Request path that caused the error", example = "/api/test/secure")
    private String path;
    
    @Schema(description = "Error message", example = "Token expired")
    private String message;
    
    @Schema(description = "Additional error details", example = "[\"JWT_EXPIRED\"]")
    private List<String> details;
}