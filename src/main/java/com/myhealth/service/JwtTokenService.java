package com.myhealth.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.UUID;

public interface JwtTokenService {
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UUID userId);
    Claims parseClaims(String token);
    boolean isTokenExpired(String token);
    String getUsername(String token);
    boolean validateToken(String token);
    void revokeRefreshToken(String refreshToken);
    void revokeAllUserTokens(UUID userId);
}