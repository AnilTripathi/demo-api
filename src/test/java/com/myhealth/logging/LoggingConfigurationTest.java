package com.myhealth.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("debug")
public class LoggingConfigurationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfigurationTest.class);
    
    @Test
    public void testDebugLoggingIsEnabled() {
        // Test different log levels
        logger.trace("TRACE level message - should not appear in DEBUG mode");
        logger.debug("DEBUG level message - should appear in DEBUG mode");
        logger.info("INFO level message - should appear");
        logger.warn("WARN level message - should appear");
        logger.error("ERROR level message - should appear");
        
        // Verify debug is enabled
        assert logger.isDebugEnabled() : "Debug logging should be enabled in debug profile";
        assert logger.isInfoEnabled() : "Info logging should be enabled";
        assert logger.isWarnEnabled() : "Warn logging should be enabled";
        assert logger.isErrorEnabled() : "Error logging should be enabled";
    }
    
    @Test
    public void testApplicationSpecificLogging() {
        Logger appLogger = LoggerFactory.getLogger("com.myhealth.TestClass");
        
        appLogger.debug("Application debug message");
        appLogger.info("Application info message");
        
        assert appLogger.isDebugEnabled() : "Application debug logging should be enabled";
    }
    
    @Test
    public void testFrameworkLogging() {
        Logger springLogger = LoggerFactory.getLogger("org.springframework.TestClass");
        Logger hibernateLogger = LoggerFactory.getLogger("org.hibernate.TestClass");
        
        springLogger.debug("Spring framework debug message");
        hibernateLogger.debug("Hibernate debug message");
        
        assert springLogger.isDebugEnabled() : "Spring debug logging should be enabled in debug profile";
        assert hibernateLogger.isDebugEnabled() : "Hibernate debug logging should be enabled in debug profile";
    }
}