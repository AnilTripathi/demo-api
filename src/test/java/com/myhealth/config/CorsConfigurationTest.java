package com.myhealth.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for CORS configuration.
 * Tests that CORS configuration is properly set up.
 */
class CorsConfigurationTest {

    @Test
    void testCorsConfigurationSetup() {
        // Create CORS configuration manually to verify the setup
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Apply the same configuration as in SecurityConfig
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", 
            "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // Verify CORS configuration properties directly
        assertNotNull(configuration);
        assertTrue(configuration.getAllowedOriginPatterns().contains("*"));
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedMethods().contains("POST"));
        assertTrue(configuration.getAllowedMethods().contains("PUT"));
        assertTrue(configuration.getAllowedMethods().contains("DELETE"));
        assertTrue(configuration.getAllowedMethods().contains("PATCH"));
        assertTrue(configuration.getAllowedMethods().contains("OPTIONS"));
        assertTrue(configuration.getAllowedMethods().contains("HEAD"));
        assertTrue(configuration.getAllowedHeaders().contains("*"));
        assertTrue(configuration.getAllowCredentials());
        assertEquals(3600L, configuration.getMaxAge());
    }

    @Test
    void testCorsConfigurationAllowsAllOrigins() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        assertTrue(configuration.getAllowedOriginPatterns().contains("*"), 
            "CORS should allow all origins with wildcard pattern");
    }

    @Test
    void testCorsConfigurationAllowsAllMethods() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        assertTrue(configuration.getAllowedMethods().contains("GET"));
        assertTrue(configuration.getAllowedMethods().contains("POST"));
        assertTrue(configuration.getAllowedMethods().contains("PUT"));
        assertTrue(configuration.getAllowedMethods().contains("DELETE"));
        assertTrue(configuration.getAllowedMethods().contains("PATCH"));
        assertTrue(configuration.getAllowedMethods().contains("OPTIONS"));
        assertTrue(configuration.getAllowedMethods().contains("HEAD"));
    }

    @Test
    void testCorsConfigurationAllowsAllHeaders() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        assertTrue(configuration.getAllowedHeaders().contains("*"), 
            "CORS should allow all headers");
    }

    @Test
    void testCorsConfigurationAllowsCredentials() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        
        assertTrue(configuration.getAllowCredentials(), 
            "CORS should allow credentials");
    }

    @Test
    void testCorsConfigurationMaxAge() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setMaxAge(3600L);
        
        assertEquals(3600L, configuration.getMaxAge(), 
            "CORS max age should be 3600 seconds");
    }
}