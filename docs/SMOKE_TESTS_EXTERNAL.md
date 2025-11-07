# External Smoke Tests Implementation

## Overview

The smoke tests have been refactored to run against an **already-running application** without starting Spring Boot context. This provides faster, more realistic testing against deployed instances.

## Key Changes

### ✅ Removed Spring Boot Dependencies
- Removed `@SpringBootTest`, `@CucumberContextConfiguration`, and all Spring annotations
- Eliminated Spring Boot context initialization
- No more embedded Tomcat or H2 database startup

### ✅ Introduced RestAssured HTTP Client
- Replaced `TestRestTemplate` with RestAssured for HTTP calls
- Direct HTTP communication with running application
- Lightweight, fast execution

### ✅ Externalized Configuration
- Base URL configurable via:
  - Environment variable: `APP_BASE_URL`
  - System property: `app.url`
  - Default: `http://localhost:8089`

### ✅ Health Check Integration
- Automatic verification that application is running
- Fails fast if application is unreachable
- Configurable retry logic

## Usage Examples

### Local Development
```bash
# Start application
java -jar target/myhealth-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

# Run smoke tests (separate terminal)
./run-smoke-tests.sh
```

### Docker Environment
```bash
# Start with Docker
docker run -d -p 8089:8089 -e SPRING_PROFILES_ACTIVE=test myhealth-api:latest

# Run smoke tests
./run-smoke-tests.sh
```

### Different Environments
```bash
# Staging environment
./run-smoke-tests.sh https://staging.myhealth.com

# Custom port
./run-smoke-tests.sh http://localhost:8080
```

### CI/CD Pipeline
```bash
# Start application in background
java -jar app.jar --spring.profiles.active=test &

# Wait and run tests
./run-smoke-tests.sh

# Cleanup
kill $APP_PID
```

## Benefits

1. **Faster Execution**: No Spring Boot startup (~30s vs ~90s)
2. **Realistic Testing**: Tests actual deployed application
3. **Environment Flexibility**: Can test any running instance
4. **CI/CD Friendly**: Easy integration with deployment pipelines
5. **Resource Efficient**: No database or heavy framework initialization

## Test Structure

- **Runner**: Plain JUnit + Cucumber (no Spring)
- **HTTP Client**: RestAssured
- **Configuration**: Environment variables/system properties
- **Context**: Singleton pattern for scenario data
- **Features**: Unchanged - same .feature files

## Migration Checklist

- ✅ Removed Spring Boot test dependencies
- ✅ Updated all step definitions to use RestAssured
- ✅ Created external configuration loader
- ✅ Added health check verification
- ✅ Updated Maven profile to exclude Spring
- ✅ Created run script with retry logic
- ✅ Updated documentation and examples
- ✅ Added CI/CD workflow example

## Troubleshooting

### Application Not Running
```
❌ Application failed to start within 60 seconds
   Health endpoint: http://localhost:8089/actuator/health
```
**Solution**: Ensure application is running and health endpoint is accessible.

### Wrong Base URL
```
Failed to connect to application at http://localhost:8089
```
**Solution**: Check base URL configuration and application port.

### Authentication Issues
```
401 Unauthorized
```
**Solution**: Verify test user credentials exist in the running application.