# CORS Configuration Guide

## Overview

This application includes a **global CORS (Cross-Origin Resource Sharing) configuration** that allows cross-origin requests from any domain, with any HTTP method and headers. This configuration is implemented in `SecurityConfig.java` and applies to all endpoints.

## ⚠️ Important Security Notice

**The current CORS configuration is designed for DEVELOPMENT and TESTING only.**

- ✅ **Development**: Allows frontend applications running on different ports/domains to access the API
- ✅ **Testing**: Enables cross-origin testing from browsers and third-party tools
- ❌ **Production**: This permissive configuration is NOT secure for production environments

## Current Configuration

The CORS configuration in `SecurityConfig.corsConfigurationSource()` currently allows:

- **Origins**: `*` (all origins)
- **Methods**: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`, `HEAD`
- **Headers**: `*` (all headers)
- **Credentials**: `true` (cookies and authorization headers allowed)
- **Max Age**: `3600` seconds (1 hour preflight cache)

## Testing CORS Configuration

### 1. Browser-Based Testing

Open browser developer tools and make a cross-origin request:

```javascript
// From browser console on a different domain (e.g., http://localhost:3000)
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'testuser',
    password: 'password'
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

### 2. cURL Preflight Testing

Test OPTIONS preflight requests:

```bash
# Test preflight request
curl -v -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization"

# Expected response headers:
# Access-Control-Allow-Origin: http://example.com
# Access-Control-Allow-Methods: GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD
# Access-Control-Allow-Headers: *
# Access-Control-Allow-Credentials: true
# Access-Control-Max-Age: 3600
```

### 3. Actual Request Testing

```bash
# Test actual cross-origin request
curl -v -X POST http://localhost:8080/api/auth/login \
  -H "Origin: http://example.com" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}'
```

### 4. Frontend Integration Testing

Test with a frontend application running on a different port:

```javascript
// React/Vue/Angular app running on http://localhost:3000
const response = await fetch('http://localhost:8080/api/users', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  credentials: 'include' // Include cookies if needed
});
```

### 5. Automated Testing Script

Use the provided test script to verify CORS configuration:

```bash
# Make sure the application is running first
./test-cors.sh
```

This script tests:
- OPTIONS preflight requests
- Actual cross-origin requests
- Different origin patterns
- Expected CORS headers

## Production Configuration

Before deploying to production, update the CORS configuration in `SecurityConfig.java`:

### Restrictive Production Example

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Restrict to specific domains
    configuration.setAllowedOrigins(Arrays.asList(
        "https://myapp.com",
        "https://www.myapp.com",
        "https://admin.myapp.com"
    ));
    
    // Restrict to required methods only
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE"
    ));
    
    // Restrict to specific headers
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"
    ));
    
    // Set credentials based on requirements
    configuration.setAllowCredentials(true);
    
    // Shorter cache for production
    configuration.setMaxAge(1800L); // 30 minutes
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Environment-Based Configuration

For different environments, consider using application properties:

```yaml
# application.yml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}
  allowed-methods: ${CORS_ALLOWED_METHODS:GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD}
  allowed-headers: ${CORS_ALLOWED_HEADERS:*}
  allow-credentials: ${CORS_ALLOW_CREDENTIALS:true}
  max-age: ${CORS_MAX_AGE:3600}
```

## Common CORS Issues and Solutions

### 1. Credentials with Wildcard Origins

**Issue**: Browser blocks requests when `allowCredentials: true` and `allowedOrigins: "*"`

**Solution**: Use `allowedOriginPatterns` instead of `allowedOrigins` (already implemented)

### 2. Missing Preflight Headers

**Issue**: Browser sends preflight OPTIONS request but gets 403/404

**Solution**: Ensure OPTIONS method is allowed and endpoint permits unauthenticated OPTIONS requests

### 3. Custom Headers Not Allowed

**Issue**: Custom headers like `X-API-Key` are blocked

**Solution**: Add specific headers to `allowedHeaders` or use `"*"` for development

## Security Best Practices

1. **Never use wildcard origins in production**
2. **Minimize allowed methods** to only what's needed
3. **Specify exact headers** instead of allowing all
4. **Set appropriate max-age** for preflight caching
5. **Use HTTPS origins** in production
6. **Monitor CORS logs** for suspicious cross-origin requests

## Troubleshooting

### Enable CORS Debug Logging

Add to `application.yml`:

```yaml
logging:
  level:
    org.springframework.web.cors: DEBUG
    org.springframework.security.web.access: DEBUG
```

### Common Error Messages

- `"CORS policy: No 'Access-Control-Allow-Origin' header"` → Check origin configuration
- `"CORS policy: Method not allowed"` → Add method to allowedMethods
- `"CORS policy: Request header not allowed"` → Add header to allowedHeaders
- `"CORS policy: Credentials not supported"` → Check allowCredentials setting

## Related Files

- `src/main/java/com/myhealth/config/SecurityConfig.java` - Main CORS configuration
- `src/test/java/com/myhealth/config/CorsConfigurationTest.java` - Unit tests for CORS configuration
- `test-cors.sh` - Shell script for testing CORS with curl
- `src/main/resources/application.yml` - Application configuration
- `src/test/resources/application-test.yml` - Test configuration

## References

- [Spring Security CORS Documentation](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html)
- [MDN CORS Documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Boot CORS Guide](https://spring.io/guides/gs/rest-service-cors/)