package com.myhealth.exception;

import com.myhealth.dto.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, WebRequest request) {
        log.error("API Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            ex.getStatus().value(),
            request.getDescription(false).replace("uri=", ""),
            ex.getMessage(),
            Collections.singletonList(ex.getErrorCode())
        );
        
        return ResponseEntity.status(ex.getStatus()).body(apiError);
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        log.error("JWT Token expired: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false).replace("uri=", ""),
            "Token expired",
            Collections.singletonList("JWT_EXPIRED")
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }
    
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex, WebRequest request) {
        log.error("JWT Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false).replace("uri=", ""),
            "Token invalid",
            Collections.singletonList("JWT_INVALID")
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.error("Authentication Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", ""),
            "Invalid username or password",
            Collections.singletonList("AUTHENTICATION_FAILED")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation Exception: {}", ex.getMessage());
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", ""),
            "Validation failed",
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        log.error("Duplicate Resource Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            request.getDescription(false).replace("uri=", ""),
            ex.getMessage(),
            Collections.singletonList("DUPLICATE_RESOURCE")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource Not Found Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", ""),
            ex.getMessage(),
            Collections.singletonList("RESOURCE_NOT_FOUND")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex, WebRequest request) {
        log.error("Response Status Exception: {}", ex.getMessage());
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            ex.getStatusCode().value(),
            request.getDescription(false).replace("uri=", ""),
            ex.getReason() != null ? ex.getReason() : "An error occurred",
            Collections.singletonList(ex.getStatusCode().toString())
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", ""),
            "An unexpected error occurred",
            Collections.singletonList("INTERNAL_SERVER_ERROR")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}