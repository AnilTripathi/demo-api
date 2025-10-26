package com.myhealth.impl;

import com.myhealth.projection.UserLoginProjection;
import com.myhealth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        
        UserLoginProjection userProjection = userRepository.findByUsernameForLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        log.debug("User found with roles: {}", userProjection.getRoles());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(userProjection.getUsername())
                .password(userProjection.getPassword())
                .authorities(parseRoles(userProjection.getRoles()))
                .accountExpired(!Boolean.TRUE.equals(userProjection.getAccountNonExpired()))
                .accountLocked(!Boolean.TRUE.equals(userProjection.getAccountNonLocked()))
                .credentialsExpired(!Boolean.TRUE.equals(userProjection.getCredentialsNonExpired()))
                .disabled(!Boolean.TRUE.equals(userProjection.getEnabled()))
                .build();
    }
    
    /**
     * Parse comma-separated roles string into GrantedAuthority list.
     * Handles null/empty roles gracefully.
     */
    private List<SimpleGrantedAuthority> parseRoles(String roles) {
        if (roles == null || roles.trim().isEmpty()) {
            log.warn("No roles found for user, returning empty authorities");
            return Collections.emptyList();
        }
        
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}