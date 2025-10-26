package com.myhealth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test endpoints for authentication verification")
public class TestController {
    
    @Operation(summary = "Secure test endpoint", description = "Test endpoint that requires JWT authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access granted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(Authentication authentication) {
        return ResponseEntity.ok("Hello " + authentication.getName() + "! This is a secure endpoint.");
    }
}