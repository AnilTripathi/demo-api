package com.myhealth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.ApiError;
import com.myhealth.entity.User;
import com.myhealth.repository.UserRepository;
import com.myhealth.security.ApiUserDetail;
import com.myhealth.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Arrays;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            List<String> publicEndpoint = Arrays.asList("/api/auth/login", "/api/auth/refresh");
            String path = request.getRequestURI();
            String jwt = parseJwt(request);
            if (jwt != null) {
                if (publicEndpoint.contains(path)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                UUID userId = jwtTokenService.getUserId(jwt);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                ApiUserDetail userDetails = (ApiUserDetail) userDetailsService.loadUserByUsername(user.getUsername());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token expired", request.getRequestURI());
            return;
        } catch (JwtException e) {
            log.debug("JWT token invalid: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", request.getRequestURI());
            return;
        } catch (Exception e) {
            log.debug("Authentication failed: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", request.getRequestURI());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiError apiError = new ApiError(
            LocalDateTime.now(),
            status.value(),
            path,
            message,
            Collections.singletonList(status == HttpStatus.UNAUTHORIZED && message.equals("Token expired") ? "JWT_EXPIRED" : "JWT_INVALID")
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}