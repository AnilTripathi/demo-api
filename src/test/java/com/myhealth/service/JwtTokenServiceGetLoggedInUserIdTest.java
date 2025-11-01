package com.myhealth.service;

import com.myhealth.exception.UnauthorizedException;
import com.myhealth.impl.JwtTokenServiceImpl;
import com.myhealth.repository.UserTokenRepository;
import com.myhealth.security.ApiUserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceGetLoggedInUserIdTest {

    @Mock
    private UserTokenRepository userTokenRepository;

    private JwtTokenServiceImpl jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenServiceImpl(userTokenRepository);
    }

    @Test
    void getLoggedInUserId_WithValidAuthentication_ShouldReturnUserId() {
        // Given
        UUID expectedUserId = UUID.randomUUID();
        ApiUserDetail userDetail = new ApiUserDetail(
                expectedUserId,
                "testuser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true, true, true, true
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetail, null, userDetail.getAuthorities());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        UUID actualUserId = jwtTokenService.getLoggedInUserId();

        // Then
        assertThat(actualUserId).isEqualTo(expectedUserId);
    }

    @Test
    void getLoggedInUserId_WithNoAuthentication_ShouldThrowUnauthorizedException() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        assertThatThrownBy(() -> jwtTokenService.getLoggedInUserId())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Unauthorized");
    }

    @Test
    void getLoggedInUserId_WithUnauthenticatedUser_ShouldThrowUnauthorizedException() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        assertThatThrownBy(() -> jwtTokenService.getLoggedInUserId())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Unauthorized");
    }

    @Test
    void getLoggedInUserId_WithWrongPrincipalType_ShouldThrowUnauthorizedException() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("wrong-principal-type");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        assertThatThrownBy(() -> jwtTokenService.getLoggedInUserId())
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Unauthorized");
    }
}