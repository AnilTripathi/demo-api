package com.myhealth.impl;

import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.dto.RefreshRequest;
import com.myhealth.entity.User;
import com.myhealth.exception.ApiException;
import com.myhealth.model.UserToken;
import com.myhealth.repository.UserRepository;
import com.myhealth.repository.UserTokenRepository;
import com.myhealth.security.ApiUserDetail;
import com.myhealth.service.AuthService;
import com.myhealth.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    
    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;
    
    @Override
    @Transactional
    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), 
                    authRequest.getPassword()
                )
            );
            
            ApiUserDetail userDetails = (ApiUserDetail) authentication.getPrincipal();
            
            String accessToken = jwtTokenService.generateAccessToken(userDetails.getId(), userDetails.getAuthorities());
            String refreshToken = jwtTokenService.generateRefreshToken(userDetails.getId());
            
            return new AuthResponse(accessToken, refreshToken, accessTokenExpirationMs);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new ApiException("Invalid username or password", HttpStatus.BAD_REQUEST);
        }
    }
    
    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        // Validate access token signature without checking expiration
        if (!jwtTokenService.validateTokenSignature(refreshRequest.getAccessToken())) {
            throw new ApiException("Invalid token signature", HttpStatus.UNAUTHORIZED);
        }
        
        // Extract userId from access token (ignoring expiration)
        Claims claims = jwtTokenService.parseClaimsIgnoreExpiration(refreshRequest.getAccessToken());
        UUID userId = UUID.fromString(claims.getSubject());
        
        // Find and validate refresh token
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshRequest.getRefreshToken())
                .orElseThrow(() -> new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
        
        // Check if subject matches stored user id
        if (!userId.equals(userToken.getUserId())) {
            throw new ApiException("Token subject mismatch", HttpStatus.UNAUTHORIZED);
        }
        
        // Check if refresh token is expired
        if (userToken.isExpired()) {
            userTokenRepository.delete(userToken);
            throw new ApiException("Refresh token expired, please login again", HttpStatus.UNAUTHORIZED);
        }
        
        // Load user details and generate new tokens
        User user = userRepository.findById(userToken.getUserId())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        
        ApiUserDetail userDetails = (ApiUserDetail) userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtTokenService.generateAccessToken(userDetails.getId(), userDetails.getAuthorities());
        
        // Rotate refresh token
        String newRefreshToken = jwtTokenService.generateRefreshToken(user.getId());
        jwtTokenService.revokeRefreshToken(refreshRequest.getRefreshToken());
        
        return new AuthResponse(newAccessToken, newRefreshToken, accessTokenExpirationMs);
    }
    
    @Override
    @Transactional
    public void logout(String refreshToken) {
        jwtTokenService.revokeRefreshToken(refreshToken);
    }
}