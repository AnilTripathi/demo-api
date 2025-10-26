package com.myhealth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestProfileConfigurationTest {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${test.environment}")
    private String testEnvironment;
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${jwt.access-token-expiration-ms}")
    private long jwtExpiration;
    
    @Value("${test.mock-external-services}")
    private boolean mockExternalServices;
    
    @Test
    public void testProfileConfigurationIsLoaded() {
        // Verify test profile specific configurations
        assertEquals("myhealth-test", applicationName, "Application name should be set for test profile");
        assertEquals("test", testEnvironment, "Test environment should be 'test'");
        assertTrue(datasourceUrl.contains("h2:mem:testdb"), "Should use H2 in-memory database for tests");
        assertEquals(60000L, jwtExpiration, "JWT expiration should be shorter for tests");
        assertTrue(mockExternalServices, "External services should be mocked in tests");
    }
    
    @Test
    public void testDatabaseConfiguration() {
        // Verify H2 database is configured correctly
        assertTrue(datasourceUrl.startsWith("jdbc:h2:mem:"), "Should use H2 in-memory database");
        assertTrue(datasourceUrl.contains("DB_CLOSE_DELAY=-1"), "Database should not close immediately");
    }
    
    @Test
    public void testJwtConfiguration() {
        // Verify JWT configuration for tests
        assertTrue(jwtExpiration > 0, "JWT expiration should be positive");
        assertTrue(jwtExpiration < 3600000, "JWT expiration should be less than 1 hour for tests");
    }
}