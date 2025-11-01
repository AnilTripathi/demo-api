package com.myhealth.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.UUID;

public interface JwtTokenService {
    String generateAccessToken(UUID userId, Collection<? extends GrantedAuthority> authorities);
    String generateRefreshToken(UUID userId);
    Claims parseClaims(String token);
    Claims parseClaimsIgnoreExpiration(String token);
    boolean isTokenExpired(String token);
    UUID getUserId(String token);
    boolean validateToken(String token);
    boolean validateTokenSignature(String token);
    void revokeRefreshToken(String refreshToken);
    void revokeAllUserTokens(UUID userId);
    
    /**
     * Extracts the logged-in user's UUID from the current security context.
     * This should be the single source of truth for logged-in user identity in the service layer.
     * 
     * @return UUID of the currently authenticated user
     * @throws RuntimeException if no authentication is present or invalid
     */
    UUID getLoggedInUserId();
}