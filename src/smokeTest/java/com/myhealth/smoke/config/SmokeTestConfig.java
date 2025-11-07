package com.myhealth.smoke.config;

public class SmokeTestConfig {
    
    private static final String DEFAULT_BASE_URL = "http://localhost:8089";
    
    public static String getBaseUrl() {
        // Try environment variable first
        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            return baseUrl.trim();
        }
        
        // Try system property
        baseUrl = System.getProperty("app.url");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            return baseUrl.trim();
        }
        
        // Default fallback
        return DEFAULT_BASE_URL;
    }
}