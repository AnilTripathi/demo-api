package com.myhealth.impl;

import com.myhealth.exception.UnauthorizedException;
import com.myhealth.model.UserToken;
import com.myhealth.repository.UserTokenRepository;
import com.myhealth.security.ApiUserDetail;
import com.myhealth.service.JwtTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    
    private final UserTokenRepository userTokenRepository;
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;
    
    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    @Override
    public String generateAccessToken(UUID userId, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("authorities", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    @Override
    @Transactional
    public String generateRefreshToken(UUID userId) {
        String token = UUID.randomUUID().toString();
        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setRefreshToken(token);
        userToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000));
        userTokenRepository.save(userToken);
        return token;
    }
    
    @Override
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    @Override
    public Claims parseClaimsIgnoreExpiration(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
    
    @Override
    public UUID getUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    @Override
    public boolean validateTokenSignature(String token) {
        try {
            parseClaimsIgnoreExpiration(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        userTokenRepository.deleteByRefreshToken(refreshToken);
    }
    
    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        userTokenRepository.deleteByUserId(userId);
    }
    
    @Override
    public UUID getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof ApiUserDetail)) {
            throw new UnauthorizedException();
        }
        
        return ((ApiUserDetail) principal).getId();
    }
}