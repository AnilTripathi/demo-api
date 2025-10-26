# OpenAPI Documentation

This project includes comprehensive OpenAPI 3.0 documentation with Swagger UI integration.

## Accessing the Documentation

### Swagger UI (Interactive Documentation)
- **URL**: http://localhost:8089/swagger-ui.html
- **Description**: Interactive web interface to explore and test API endpoints
- **Features**: Try out endpoints, view request/response schemas, authentication support

### OpenAPI JSON Specification
- **URL**: http://localhost:8089/v3/api-docs
- **Description**: Machine-readable OpenAPI 3.0 specification in JSON format
- **Usage**: Import into API clients, code generators, or testing tools

### OpenAPI YAML Specification
- **URL**: http://localhost:8089/v3/api-docs.yaml
- **Description**: Machine-readable OpenAPI 3.0 specification in YAML format

## Configuration

### Application Properties
Configure API metadata via `application.yml`:

```yaml
springdoc:
  info:
    title: ${API_TITLE:MyHealth API}
    description: ${API_DESCRIPTION:REST API for MyHealth application}
    version: ${API_VERSION:1.0.0}
    contact:
      name: ${API_CONTACT_NAME:MyHealth Team}
      email: ${API_CONTACT_EMAIL:support@myhealth.com}
```

### Environment Variables
- `API_TITLE`: API title (default: MyHealth API)
- `API_DESCRIPTION`: API description
- `API_VERSION`: API version (default: 1.0.0)
- `API_CONTACT_NAME`: Contact name
- `API_CONTACT_EMAIL`: Contact email

## Authentication in Swagger UI

1. Click the "Authorize" button in Swagger UI
2. Enter your JWT token in the format: `Bearer <your-jwt-token>`
3. Click "Authorize" to apply the token to all requests

### Getting a JWT Token
1. Use the `/api/auth/login` endpoint with valid credentials
2. Copy the `accessToken` from the response
3. Use it in the Authorization header: `Bearer <accessToken>`

## Adding Documentation to New Endpoints

### Controller Level
```java
@Tag(name = "User Management", description = "User management APIs")
@RestController
public class UserController {
    // ...
}
```

### Method Level
```java
@Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User found"),
    @ApiResponse(responseCode = "404", description = "User not found")
})
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    // ...
}
```

### DTO/Model Level
```java
@Schema(description = "User information")
public class User {
    @Schema(description = "User ID", example = "123")
    private Long id;
    
    @Schema(description = "Username", example = "john_doe", required = true)
    private String username;
}
```

## Testing

Run integration tests to verify OpenAPI endpoints:

```bash
mvn test -Dtest=OpenApiIntegrationTest
```

## Dependencies

### Maven
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0'
```

## Version Compatibility
- **Springdoc OpenAPI 2.7.0**: Compatible with Spring Boot 3.x
- **Spring Boot**: 3.5.7
- **OpenAPI Specification**: 3.0.3