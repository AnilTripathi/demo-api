package com.myhealth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/user/**").access(createContainsRoleAuthorizationManager("USER", "ADMIN"))
                .requestMatchers("/api/admin/**").access(createContainsRoleAuthorizationManager("ADMIN"))
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Global CORS Configuration for Development/Testing
     * 
     * WARNING: This configuration allows ALL origins, methods, and headers.
     * This is ONLY suitable for development and testing environments.
     * 
     * For production, restrict:
     * - allowedOrigins to specific domains
     * - allowedMethods to required HTTP methods only
     * - allowedHeaders to specific headers
     * - Set allowCredentials based on authentication requirements
     * 
     * @return CorsConfigurationSource with permissive settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins (development only)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Expose common headers that clients might need
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", 
            "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        
        // Allow credentials (cookies, authorization headers)
        // Note: When allowedOrigins is "*", allowCredentials should be false
        // Using allowedOriginPatterns with "*" allows credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Creates a contains-based authorization manager that grants access if ANY granted authority
     * contains one of the target keywords (case-insensitive).
     * 
     * This replaces hasAnyRole enumeration and supports flexible role matching:
     * - ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN
     * - USER_MANAGER, ADMIN_VIEWER, etc.
     * 
     * For /api/user/**: allows roles containing "USER" or "ADMIN" (admins inherit user access)
     * For /api/admin/**: allows roles containing "ADMIN" only
     * 
     * @param targetKeywords the keywords to search for in authority names
     * @return AuthorizationManager that performs contains-based role checking
     */
    private AuthorizationManager<RequestAuthorizationContext> createContainsRoleAuthorizationManager(String... targetKeywords) {
        return new ContainsRoleAuthorizationManager(targetKeywords);
    }
    
    /**
     * Custom AuthorizationManager that checks if any granted authority contains
     * one of the specified keywords (case-insensitive).
     */
    private static class ContainsRoleAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
        
        private final String[] targetKeywords;
        
        public ContainsRoleAuthorizationManager(String... targetKeywords) {
            this.targetKeywords = targetKeywords != null ? targetKeywords : new String[0];
        }
        
        @Override
        public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
            Authentication auth = authentication.get();
            
            // Require authentication
            if (auth == null || !auth.isAuthenticated()) {
                log.debug("Access denied: User not authenticated for path: {}", context.getRequest().getRequestURI());
                return new AuthorizationDecision(false);
            }
            
            // Check if any authority contains one of the target keywords
            for (GrantedAuthority authority : auth.getAuthorities()) {
                String authorityName = authority.getAuthority();
                if (authorityName != null) {
                    String normalizedAuthority = authorityName.trim().toUpperCase();
                    
                    for (String keyword : targetKeywords) {
                        if (keyword != null && normalizedAuthority.contains(keyword.toUpperCase())) {
                            log.debug("Access granted: Authority '{}' contains keyword '{}' for path: {}", 
                                    authorityName, keyword, context.getRequest().getRequestURI());
                            return new AuthorizationDecision(true);
                        }
                    }
                }
            }
            
            log.debug("Access denied: No authority contains required keywords {} for user '{}' on path: {}", 
                    Arrays.toString(targetKeywords), auth.getName(), context.getRequest().getRequestURI());
            return new AuthorizationDecision(false);
        }
    }
}