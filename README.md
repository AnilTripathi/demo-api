# MyHealth API

A secure REST API for healthcare management with JWT authentication, user registration, and comprehensive health data management.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/your-org/myhealth-api)
[![Coverage](https://img.shields.io/badge/coverage-85%25-green)](https://github.com/your-org/myhealth-api)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start - Local](#quick-start---local)
- [Configuration & Environment](#configuration--environment)
- [API Endpoints](#api-endpoints)
- [Admin / Test User Setup](#admin--test-user-setup)
- [Logging & Debugging](#logging--debugging)
- [CORS, CSRF & Security](#cors-csrf--security)
- [CI/CD & Docker](#cicd--docker)
- [Tests & Quality](#tests--quality)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Maintainers & Support](#maintainers--support)
- [License & Changelog](#license--changelog)

## Prerequisites

- **Java 21+** (OpenJDK or Oracle JDK)
- **Maven 3.8+** (wrapper included: `./mvnw`)
- **PostgreSQL 13+** (for production) or H2 (for testing)
- **Docker** (optional, for containerized deployment)

### Verify Prerequisites

```bash
java -version    # Should show Java 21+
./mvnw -version  # Should show Maven 3.8+
docker --version # Optional
```

## Quick Start - Local

### 1. Clone and Build

```bash
git clone https://github.com/your-org/myhealth-api.git
cd myhealth-api

# Build (skip tests for faster startup)
./mvnw clean package -DskipTests
```

### 2. Run with Test Profile (H2 Database)

```bash
# Run with test profile (uses H2 in-memory database)
java -jar target/myhealth-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```

### 3. Run with Development Profile (PostgreSQL)

```bash
# Set up PostgreSQL database first
createdb myhealthapi

# Run with development profile
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
java -jar target/myhealth-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 4. Run Tests

```bash
./mvnw test
```

### 5. Run with Docker

```bash
# Build Docker image
docker build -t myhealth-api:latest .

# Run container
docker run -e SPRING_PROFILES_ACTIVE=test -p 8089:8089 myhealth-api:latest
```

## Configuration & Environment

### Configuration Files

- **Main config**: `src/main/resources/application.yml`
- **Test profile**: `src/test/resources/application-test.yml`
- **Logging**: `src/main/resources/logback-spring.xml`

### Required Environment Variables

```bash
# Database (for dev/prod profiles)
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/myhealthapi

# JWT Security
export JWT_SECRET=your_32_char_secret_key_for_production

# Optional API Documentation
export API_TITLE="MyHealth API"
export API_CONTACT_EMAIL=support@myhealth.com
```

### Profile Usage

```bash
# Test profile (H2 database, fast startup)
--spring.profiles.active=test

# Development profile (PostgreSQL)
--spring.profiles.active=dev

# Production profile
--spring.profiles.active=prod
```

## API Endpoints

### Health & Documentation

```bash
# Health check
curl -s http://localhost:8089/actuator/health

# API Documentation (Swagger UI)
open http://localhost:8089/swagger-ui.html

# OpenAPI JSON spec
curl -s http://localhost:8089/v3/api-docs
```

### Authentication

#### Register New User

```bash
curl -X POST http://localhost:8089/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### Login

```bash
curl -X POST http://localhost:8089/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

#### Access Protected Endpoint

```bash
# Replace <TOKEN> with actual access token from login
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8089/api/users/profile
```

#### Refresh Token

```bash
curl -X POST http://localhost:8089/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

#### Logout

```bash
curl -X POST http://localhost:8089/api/auth/logout \
  -H "Authorization: Bearer <TOKEN>"
```

## Admin / Test User Setup

### Generate BCrypt Password Hash

```bash
# Using Spring Boot CLI (if installed)
spring encodepassword mypassword

# Or use online tool: https://bcrypt-generator.com/
# Or run this Java snippet:
```

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("your_password"));
    }
}
```

### Seed Admin User

The application automatically creates default roles on startup. To create an admin user, use the registration endpoint or insert directly into the database:

```sql
-- Example SQL to create admin user (password: admin123)
INSERT INTO user_profile (id, email, first_name, last_name, created_at, updated_at) 
VALUES ('admin-uuid', 'admin@myhealth.com', 'Admin', 'User', NOW(), NOW());

INSERT INTO users (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) 
VALUES ('admin-uuid', 'admin', '$2a$10$encrypted_password_hash', true, true, true, true);
```

## Logging & Debugging

### Enable Debug Logging

```bash
# Enable debug mode
java -jar target/myhealth-0.0.1-SNAPSHOT.jar --debug

# Enable Spring Security debug
java -jar target/myhealth-0.0.1-SNAPSHOT.jar \
  --logging.level.org.springframework.security=DEBUG
```

### Log Configuration

Logs are written to:
- **Console**: All profiles
- **File**: `logs/myhealth.log` (configurable)

### Debug Specific Components

```yaml
# Add to application.yml
logging:
  level:
    com.myhealth: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web.cors: DEBUG
    org.hibernate.SQL: DEBUG
```

## CORS, CSRF & Security

### CORS Configuration

**⚠️ DEVELOPMENT ONLY**: Current CORS configuration allows all origins.

```bash
# Test CORS with curl
curl -v -X OPTIONS http://localhost:8089/api/auth/login \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST"
```

For production, update `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://myapp.com",
    "https://www.myapp.com"
));
```

### Security Features

- **JWT Authentication**: Stateless authentication with access/refresh tokens
- **BCrypt Password Encoding**: Secure password hashing
- **CORS Support**: Configurable cross-origin resource sharing
- **Role-based Access Control**: User roles and permissions

## CI/CD & Docker

### GitHub Actions Example

```yaml
# .github/workflows/ci.yml
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: ./mvnw test
```

### Docker

```dockerfile
# Dockerfile
FROM openjdk:21-jre-slim
COPY target/myhealth-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build and run
docker build -t myhealth-api .
docker run -p 8089:8089 -e SPRING_PROFILES_ACTIVE=test myhealth-api
```

## Tests & Quality

### Test Structure

- **Unit Tests**: `src/test/java/com/myhealth/`
- **Integration Tests**: Use `@SpringBootTest` with test profile
- **Test Database**: H2 in-memory database

### Run Tests

```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=CorsConfigurationTest

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"

# With coverage
./mvnw test jacoco:report
```

### Test Profiles

```bash
# Run with test profile
./mvnw test -Dspring.profiles.active=test
```

## Troubleshooting

### Common Issues

#### 401 Unauthorized
```bash
# Check token validity
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8089/api/users/profile

# Verify token hasn't expired (15 min default)
# Use refresh token to get new access token
```

#### 403 Forbidden
```bash
# Check user roles
# Verify endpoint requires correct role
# Check CORS headers for browser requests
```

#### Database Connection Errors
```bash
# Verify PostgreSQL is running
pg_isready -h localhost -p 5432

# Check connection string
echo $SPRING_DATASOURCE_URL

# Use test profile for H2 database
--spring.profiles.active=test
```

#### CORS Issues
```bash
# Test CORS headers
curl -v -H "Origin: http://localhost:3000" http://localhost:8089/api/auth/login

# Check browser network tab for preflight requests
```

### Debug Commands

```bash
# Check application health
curl http://localhost:8089/actuator/health

# View application info
curl http://localhost:8089/actuator/info

# Check active profiles
curl http://localhost:8089/actuator/env | grep profiles
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make changes and add tests
4. Run tests: `./mvnw test`
5. Commit changes: `git commit -m 'Add amazing feature'`
6. Push to branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Add unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## Maintainers & Support

- **Primary Maintainer**: MyHealth Team
- **Email**: support@myhealth.com
- **Response Time**: 1-2 business days

For urgent issues, please include:
- Application logs
- Steps to reproduce
- Environment details (Java version, OS, etc.)

## License & Changelog

- **License**: MIT License - see [LICENSE](LICENSE) file
- **Changelog**: See [CHANGELOG.md](docs/CHANGELOG.md) for version history
- **Documentation**: Additional docs in [docs/](docs/) folder

---

## Quick Reference

```bash
# Start application (test mode)
java -jar target/myhealth-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

# Register user
curl -X POST http://localhost:8089/api/auth/register -H "Content-Type: application/json" -d '{"username":"test","password":"pass123","email":"test@example.com","firstName":"Test","lastName":"User"}'

# Login
curl -X POST http://localhost:8089/api/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"pass123"}'

# Access protected endpoint
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8089/api/users/profile

# Health check
curl http://localhost:8089/actuator/health

# API docs
open http://localhost:8089/swagger-ui.html
```