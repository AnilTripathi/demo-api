# Debug Logging Configuration

This project includes comprehensive debug logging configuration for development and troubleshooting purposes.

## Overview

The logging system provides multiple ways to enable debug logging:
- **Environment Variables**: Configure logging levels via environment variables
- **Debug Profile**: Dedicated `debug` profile with comprehensive debug logging
- **Command Line Flags**: Use Spring Boot's `--debug` flag
- **Logback Configuration**: Profile-specific logging with file rotation

## Configuration Files

### `application.yml`
Main configuration with environment variable support:
```yaml
logging:
  level:
    root: ${LOGGING_LEVEL_ROOT:INFO}
    com.myhealth: ${LOGGING_LEVEL_APP:INFO}
    org.springframework: ${LOGGING_LEVEL_SPRING:INFO}
```

### `application-debug.yml`
Debug profile with comprehensive debug logging enabled.

### `logback-spring.xml`
Advanced Logback configuration with:
- Profile-specific logging
- File rotation
- Enhanced debug patterns with line numbers

## Enabling Debug Logging

### Method 1: Debug Profile
```bash
# Start application with debug profile
java -jar myhealth.jar --spring.profiles.active=debug

# Or with Maven
mvn spring-boot:run -Dspring-boot.run.profiles=debug

# Or with environment variable
export SPRING_PROFILES_ACTIVE=debug
java -jar myhealth.jar
```

### Method 2: Environment Variables
```bash
# Set specific logging levels
export LOGGING_LEVEL_ROOT=DEBUG
export LOGGING_LEVEL_APP=DEBUG
export LOGGING_LEVEL_SPRING=DEBUG
java -jar myhealth.jar

# Or inline
LOGGING_LEVEL_ROOT=DEBUG LOGGING_LEVEL_APP=DEBUG java -jar myhealth.jar
```

### Method 3: Spring Boot Debug Flag
```bash
# Enable Spring Boot's built-in debug logging
java -jar myhealth.jar --debug

# Or with Maven
mvn spring-boot:run -Dspring-boot.run.arguments=--debug
```

### Method 4: IDE Configuration
Set VM options in your IDE:
```
-Dspring.profiles.active=debug
-DLOGGING_LEVEL_ROOT=DEBUG
-DLOGGING_LEVEL_APP=DEBUG
```

## Logging Levels Available

### Environment Variables
- `LOGGING_LEVEL_ROOT`: Root logging level (default: INFO)
- `LOGGING_LEVEL_APP`: Application logging level (default: INFO)
- `LOGGING_LEVEL_SPRING`: Spring framework logging (default: INFO)
- `LOGGING_LEVEL_SECURITY`: Spring Security logging (default: INFO)
- `LOGGING_LEVEL_WEB`: Spring Web logging (default: INFO)
- `LOGGING_LEVEL_HIBERNATE`: Hibernate logging (default: WARN)
- `LOGGING_LEVEL_SQL`: SQL logging (default: WARN)
- `LOGGING_LEVEL_FLYWAY`: Flyway migration logging (default: INFO)
- `LOGGING_LEVEL_HIKARI`: HikariCP connection pool logging (default: INFO)
- `LOGGING_LEVEL_JWT`: JWT library logging (default: INFO)

### Debug Profile Levels
When using the `debug` profile, all levels are set to DEBUG or TRACE for maximum visibility.

## Log Output Locations

### Console Output
All logging levels output to console with formatted timestamps and thread information.

### File Output
- **Default**: `logs/myhealth.log`
- **Debug Profile**: `logs/myhealth-debug.log`
- **Custom Location**: Set `LOGGING_FILE` environment variable

### Log Rotation
- **Max File Size**: 10MB (default), 50MB (debug)
- **Max History**: 30 days (default), 7 days (debug)
- **Total Size Cap**: 1GB (default), 500MB (debug)

## Log Patterns

### Default Pattern
```
2024-01-01 12:00:00.123 [main] INFO  com.myhealth.Application - Application started
```

### Debug Pattern (with line numbers)
```
2024-01-01 12:00:00.123 [main] DEBUG com.myhealth.Application:25 - Debug message
```

## Debugging Specific Components

### Database Queries
```bash
# Enable SQL logging
export LOGGING_LEVEL_SQL=DEBUG
export LOGGING_LEVEL_HIBERNATE=DEBUG

# Or use debug profile (includes SQL formatting)
java -jar myhealth.jar --spring.profiles.active=debug
```

### Security
```bash
# Enable security debug logging
export LOGGING_LEVEL_SECURITY=DEBUG
java -jar myhealth.jar
```

### Web Requests
```bash
# Enable web request logging
export LOGGING_LEVEL_WEB=DEBUG
java -jar myhealth.jar
```

### JWT Processing
```bash
# Enable JWT debug logging
export LOGGING_LEVEL_JWT=DEBUG
java -jar myhealth.jar
```

## Production Considerations

### Security
- **Never log sensitive data** (passwords, tokens, personal information)
- **Review debug logs** before deploying to production
- **Use INFO or WARN** levels in production environments

### Performance
- **Debug logging impacts performance** - use only when needed
- **File I/O overhead** - monitor disk space with debug logging
- **Memory usage** - verbose logging can increase memory consumption

### Best Practices
```bash
# Production environment
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_LEVEL_APP=INFO

# Staging environment
export LOGGING_LEVEL_ROOT=INFO
export LOGGING_LEVEL_APP=DEBUG

# Development environment
export SPRING_PROFILES_ACTIVE=debug
```

## Testing Debug Configuration

### Run Logging Tests
```bash
# Test debug profile configuration
mvn test -Dtest=LoggingConfigurationTest

# Test with debug profile active
mvn test -Dspring.profiles.active=debug
```

### Verify Debug Output
1. Start application with debug enabled
2. Check console output for DEBUG messages
3. Verify log files are created in `logs/` directory
4. Confirm log rotation is working

## Troubleshooting

### Debug Logs Not Appearing
1. **Check Profile**: Verify debug profile is active
2. **Check Environment Variables**: Ensure logging levels are set correctly
3. **Check Logback Configuration**: Verify `logback-spring.xml` is loaded
4. **Check File Permissions**: Ensure application can write to logs directory

### Too Much Output
1. **Reduce Logging Levels**: Set specific packages to WARN or ERROR
2. **Use Targeted Debugging**: Enable debug only for specific components
3. **Filter Logs**: Use log filtering tools or grep

### Performance Issues
1. **Disable File Logging**: Use console-only logging for development
2. **Reduce Log Levels**: Use INFO instead of DEBUG for non-critical components
3. **Async Logging**: Consider async appenders for high-volume logging

## Examples

### Debug Authentication Issues
```bash
export LOGGING_LEVEL_SECURITY=DEBUG
export LOGGING_LEVEL_JWT=DEBUG
java -jar myhealth.jar
```

### Debug Database Issues
```bash
export LOGGING_LEVEL_SQL=DEBUG
export LOGGING_LEVEL_HIBERNATE=DEBUG
java -jar myhealth.jar
```

### Debug Application Logic
```bash
export LOGGING_LEVEL_APP=DEBUG
java -jar myhealth.jar
```

### Full Debug Mode
```bash
java -jar myhealth.jar --spring.profiles.active=debug
```