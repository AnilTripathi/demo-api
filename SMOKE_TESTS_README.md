# Smoke Tests

This document describes the Cucumber-based smoke test suite for the MyHealth API.

## Overview

The smoke test suite provides fast, lightweight end-to-end verification of critical application functionality using Behavior-Driven Development (BDD) with Cucumber.

## Structure

```
src/smokeTest/
├── java/com/myhealth/smoke/
│   ├── runner/SmokeTestRunner.java          # JUnit 5 test runner
│   ├── config/SmokeTestConfiguration.java   # Test configuration
│   ├── hooks/SmokeTestHooks.java           # Setup/teardown hooks
│   └── steps/                              # Step definitions
│       ├── HealthSteps.java
│       ├── AuthSteps.java
│       ├── UserSteps.java
│       └── AdminSteps.java
└── resources/
    ├── config/application-smoke.yml         # Smoke test configuration
    └── features/                           # Gherkin feature files
        ├── health.feature
        ├── auth.feature
        ├── user.feature
        └── admin.feature
```

## Features Covered

### Health Check (`health.feature`)
- ✅ `/actuator/health` returns 200 with status UP
- ✅ `/actuator/info` is accessible

### Authentication (`auth.feature`)
- ✅ User registration with valid credentials
- ✅ User login with valid credentials returns tokens
- ✅ Login fails with invalid credentials

### User Management (`user.feature`)
- ✅ Get current user profile (authenticated)
- ✅ Get all users requires authentication (401)
- ✅ Get all users with authentication (200)

### Admin Access Control (`admin.feature`)
- ✅ Admin endpoints return 403 for regular users
- ✅ Admin endpoints return 201/200 for admin users

## Running Smoke Tests

### Local Execution

```bash
# Run only smoke tests
mvn test -Psmoke-tests

# Run with specific tags
mvn test -Psmoke-tests -Dcucumber.filter.tags="@smoke"

# Run with verbose output
mvn test -Psmoke-tests -Dcucumber.plugin="pretty,json:target/cucumber-reports/Cucumber.json"
```

### CI/CD Integration

The smoke tests are configured to run automatically in CI pipelines:

```yaml
# Example GitHub Actions step
- name: Run Smoke Tests
  run: mvn test -Psmoke-tests
  
- name: Publish Test Results
  uses: dorny/test-reporter@v1
  if: always()
  with:
    name: Smoke Test Results
    path: target/cucumber-reports/Cucumber.xml
    reporter: java-junit
```

## Configuration

### Test Profile (`application-smoke.yml`)
- **Database**: H2 in-memory for fast startup
- **Logging**: WARN level to reduce noise
- **Security**: Minimal test users (regular + admin)
- **Port**: Random port (0) for parallel execution
- **Flyway**: Disabled (uses JPA DDL)

### Test Data
Automatically seeded in `SmokeTestHooks`:
- **Regular User**: `smokeuser` / `smokepass`
- **Admin User**: `smokeadmin` / `adminpass`

## Adding New Smoke Tests

### 1. Create Feature File with Data Tables
```gherkin
@smoke
Feature: New Feature
  Scenario Outline: New scenario with test data
    Given the request payload:
      | field1 | <value1> |
      | field2 | <value2> |
    When I call the endpoint
    Then the response status should be <status>
    
    Examples:
      | value1 | value2 | status |
      | test1  | data1  | 200    |
      | test2  | data2  | 201    |
```

### 2. Implement Step Definitions with DataTable
```java
@RequiredArgsConstructor
public class NewSteps {
    private final TestRestTemplate restTemplate;
    private final SmokeTestHooks hooks;
    private final ScenarioContext scenarioContext;
    
    @Given("the request payload:")
    public void the_request_payload(DataTable dataTable) {
        Map<String, String> payload = DataTableUtil.toMap(dataTable);
        scenarioContext.setData("payload", payload);
    }
}
```

### 3. Data-Driven Testing Rules
- **ALL test data must be defined in .feature files**
- **NO hardcoded values in Java step definitions**
- Use DataTable for complex data structures
- Use Scenario Outline with Examples for multiple test cases
- Store data in ScenarioContext for sharing between steps

### 3. Tag Management
- `@smoke`: Include in smoke test suite
- `@wip`: Work in progress (excluded by default)
- `@slow`: Slower tests (run separately)

## Best Practices

### Test Design
- ✅ Keep scenarios independent and stateless
- ✅ Use meaningful scenario names
- ✅ Focus on API contracts, not implementation
- ✅ Verify status codes and essential response fields
- ✅ Define ALL test data in .feature files using DataTable or Examples
- ✅ Use ScenarioContext to share data between steps
- ❌ Avoid deep data setup or complex assertions
- ❌ NEVER hardcode test data in Java step definitions

### Performance
- ✅ Target < 90 seconds total execution time
- ✅ Use H2 database for speed
- ✅ Minimal logging configuration
- ✅ Reuse Spring context across scenarios

### Reliability
- ✅ Deterministic test data
- ✅ No external dependencies
- ✅ Proper cleanup between scenarios
- ✅ Clear error messages

## Troubleshooting

### Common Issues

**Tests fail with "Connection refused"**
```bash
# Check if application starts correctly
mvn spring-boot:run -Dspring-boot.run.profiles=smoke
```

**Authentication failures**
```bash
# Verify test users are created
# Check SmokeTestHooks.setUp() method
```

**Port conflicts**
```bash
# Ensure random port configuration
server.port: 0
```

### Debug Mode
```bash
# Run with debug logging
mvn test -Psmoke-tests -Dlogging.level.com.myhealth=DEBUG
```

## Reports

Test reports are generated in:
- **Console**: Pretty format during execution
- **JSON**: `target/cucumber-reports/Cucumber.json`
- **JUnit XML**: `target/cucumber-reports/Cucumber.xml`

## Execution Time Targets

| Component | Target Time |
|-----------|-------------|
| Application Startup | < 30s |
| Test Execution | < 45s |
| Cleanup | < 15s |
| **Total** | **< 90s** |

## Integration with Main Test Suite

Smoke tests are separate from unit/integration tests:
- **Unit Tests**: `mvn test` (excludes smoke tests)
- **Integration Tests**: `mvn verify` (includes smoke tests)
- **Smoke Only**: `mvn test -Psmoke-tests`