# Test Profile Configuration

This project includes a dedicated test configuration profile using `application-test.yml` for isolated test environments.

## Overview

The test profile provides:
- **In-memory H2 database** for fast, isolated tests
- **Optimized configurations** for test performance
- **Disabled external services** to avoid dependencies
- **Cleaner logging** for better test output
- **Shorter JWT expiration** for faster test cycles

## Files

### `src/test/resources/application-test.yml`
Main test configuration file that overrides production settings with test-specific values.

### Test Classes Using Profile
- `OpenApiIntegrationTest` - API documentation tests
- `TestProfileConfigurationTest` - Verifies test profile is loaded correctly

## Usage

### Automatic Activation
Tests automatically use the test profile when annotated with:
```java
@ActiveProfiles("test")
```

### Manual Activation
Run tests with test profile explicitly:
```bash
mvn test -Dspring.profiles.active=test
```

### IDE Configuration
Set VM options in your IDE:
```
-Dspring.profiles.active=test
```

## Configuration Details

### Database
- **Type**: H2 in-memory database
- **URL**: `jdbc:h2:mem:testdb`
- **Schema**: Auto-created and dropped per test
- **Flyway**: Disabled (using JPA create-drop instead)

### JWT Settings
- **Access Token**: 1 minute expiration (vs 15 minutes in prod)
- **Refresh Token**: 5 minutes expiration (vs 7 days in prod)
- **Secret**: Test-specific secret key

### Logging
- **Root Level**: WARN (reduces noise)
- **Application Level**: INFO (for debugging)
- **Framework Levels**: WARN (Spring, Hibernate, etc.)

### Test-Specific Properties
```yaml
test:
  environment: test
  mock-external-services: true
  api:
    base-url: http://localhost:${server.port}
  database:
    cleanup-after-test: true
```

## Adding New Test Configurations

### 1. Update application-test.yml
Add new properties under appropriate sections:
```yaml
# Example: Adding Redis mock configuration
spring:
  redis:
    host: localhost
    port: 6370  # Different port for test Redis
    
test:
  redis:
    mock-enabled: true
```

### 2. Use in Test Classes
```java
@SpringBootTest
@ActiveProfiles("test")
public class MyServiceTest {
    
    @Value("${test.redis.mock-enabled}")
    private boolean redisMockEnabled;
    
    @Test
    public void testWithMockedRedis() {
        // Test implementation
    }
}
```

### 3. Environment-Specific Overrides
Create additional test profiles for specific scenarios:
- `application-test-integration.yml` - For integration tests
- `application-test-performance.yml` - For performance tests

## Best Practices

### 1. Keep Tests Isolated
- Use `@Transactional` and `@Rollback` for database tests
- Clean up test data after each test
- Use random ports to avoid conflicts

### 2. Mock External Dependencies
```java
@MockBean
private ExternalService externalService;
```

### 3. Use Test Containers (Optional)
For more realistic database testing:
```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:13:///testdb
```

### 4. Profile-Specific Beans
```java
@Profile("test")
@Component
public class MockEmailService implements EmailService {
    // Mock implementation
}
```

## Troubleshooting

### Profile Not Loading
1. Verify `@ActiveProfiles("test")` annotation
2. Check `application-test.yml` is in `src/test/resources`
3. Ensure no syntax errors in YAML file

### Database Issues
1. Check H2 dependency is in test scope
2. Verify database URL format
3. Enable SQL logging: `spring.jpa.show-sql: true`

### Property Not Found
1. Verify property exists in `application-test.yml`
2. Check property name spelling and case
3. Use `@Value` with default: `@Value("${test.property:defaultValue}")`

## Running Tests

### All Tests
```bash
mvn test
```

### Specific Test Class
```bash
mvn test -Dtest=TestProfileConfigurationTest
```

### With Debug Logging
```bash
mvn test -Dlogging.level.com.myhealth=DEBUG
```

### Integration Tests Only
```bash
mvn test -Dtest=**/*IntegrationTest
```

## Verification

Run the configuration test to verify setup:
```bash
mvn test -Dtest=TestProfileConfigurationTest
```

This test validates:
- Test profile is active
- H2 database is configured
- JWT settings are applied
- Custom test properties are loaded