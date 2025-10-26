package com.myhealth.service;

import com.myhealth.impl.CustomUserDetailsServiceImpl;
import com.myhealth.projection.UserLoginProjection;
import com.myhealth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceProjectionTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private CustomUserDetailsServiceImpl userDetailsService;
    
    @Mock
    private UserLoginProjection userProjection;
    
    @BeforeEach
    public void setUp() {
        when(userProjection.getId()).thenReturn(UUID.randomUUID());
        when(userProjection.getUsername()).thenReturn("test@example.com");
        when(userProjection.getPassword()).thenReturn("$2a$10$encodedPassword");
        when(userProjection.getEnabled()).thenReturn(true);
        when(userProjection.getAccountNonExpired()).thenReturn(true);
        when(userProjection.getAccountNonLocked()).thenReturn(true);
        when(userProjection.getCredentialsNonExpired()).thenReturn(true);
        when(userProjection.getRoles()).thenReturn("ROLE_USER");
    }
    
    @Test
    public void testLoadUserByUsername_Success() {
        // Given
        when(userRepository.findByUsernameForLogin("test@example.com"))
                .thenReturn(Optional.of(userProjection));
        
        // When
        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("$2a$10$encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        
        verify(userRepository).findByUsernameForLogin("test@example.com");
    }
    
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsernameForLogin("nonexistent@example.com"))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> 
                userDetailsService.loadUserByUsername("nonexistent@example.com"));
        
        verify(userRepository).findByUsernameForLogin("nonexistent@example.com");
    }
    
    @Test
    public void testLoadUserByUsername_MultipleRoles() {
        // Given
        when(userProjection.getRoles()).thenReturn("ROLE_USER,ROLE_ADMIN");
        when(userRepository.findByUsernameForLogin("test@example.com"))
                .thenReturn(Optional.of(userProjection));
        
        // When
        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");
        
        // Then
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
    
    @Test
    public void testLoadUserByUsername_EmptyRoles() {
        // Given
        when(userProjection.getRoles()).thenReturn("");
        when(userRepository.findByUsernameForLogin("test@example.com"))
                .thenReturn(Optional.of(userProjection));
        
        // When
        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");
        
        // Then
        assertEquals(0, result.getAuthorities().size());
    }
    
    @Test
    public void testLoadUserByUsername_NullRoles() {
        // Given
        when(userProjection.getRoles()).thenReturn(null);
        when(userRepository.findByUsernameForLogin("test@example.com"))
                .thenReturn(Optional.of(userProjection));
        
        // When
        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");
        
        // Then
        assertEquals(0, result.getAuthorities().size());
    }
    
    @Test
    public void testLoadUserByUsername_DisabledUser() {
        // Given
        when(userProjection.getEnabled()).thenReturn(false);
        when(userRepository.findByUsernameForLogin("test@example.com"))
                .thenReturn(Optional.of(userProjection));
        
        // When
        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");
        
        // Then
        assertFalse(result.isEnabled());
    }
}