package com.myhealth.impl;

import com.myhealth.dto.AuthRequest;
import com.myhealth.dto.AuthResponse;
import com.myhealth.dto.RefreshRequest;
import com.myhealth.entity.User;
import com.myhealth.exception.ApiException;
import com.myhealth.model.UserToken;
import com.myhealth.repository.UserRepository;
import com.myhealth.repository.UserTokenRepository;
import com.myhealth.service.AuthService;
import com.myhealth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
            
            String accessToken = jwtTokenService.generateAccessToken(userDetails);
            String refreshToken = jwtTokenService.generateRefreshToken(user.getId());
            
            return new AuthResponse(accessToken, refreshToken, accessTokenExpirationMs);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new ApiException("Invalid username or password", HttpStatus.BAD_REQUEST);
        }
    }
    
    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshRequest.getRefreshToken())
                .orElseThrow(() -> new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
        
        if (userToken.isExpired()) {
            userTokenRepository.delete(userToken);
            throw new ApiException("Refresh token expired", HttpStatus.UNAUTHORIZED);
        }
        
        User user = userRepository.findById(userToken.getUserId())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtTokenService.generateAccessToken(userDetails);
        
        // Optional: Rotate refresh token
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