# User Registration API

This document describes the user registration endpoint for the MyHealth API.

## Overview

The registration endpoint allows new users to create accounts with the default role `ROLE_USER`. The system automatically sets up both user authentication credentials and user profile information.

## Endpoint

### `POST /api/auth/register`

Register a new user account.

#### Request Body

```json
{
  "email": "john.doe@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "password": "securePassword123"
}
```

#### Request Fields

| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `email` | string | Yes | Valid email format, max 255 chars | User email (used as username) |
| `firstname` | string | Yes | Non-empty, max 100 chars | User first name |
| `lastname` | string | Yes | Non-empty, max 100 chars | User last name |
| `password` | string | Yes | 6-100 characters | User password (will be encrypted) |

#### Success Response (201 Created)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john.doe@example.com",
  "firstname": "John",
  "lastname": "Doe",
  "enabled": true,
  "createdAt": "2024-01-01T12:00:00",
  "message": "User registered successfully"
}
```

#### Error Responses

##### 400 Bad Request - Validation Error
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "path": "/api/auth/register",
  "message": "Validation failed",
  "details": [
    "email: Email must be valid",
    "password: Password must be between 6 and 100 characters"
  ]
}
```

##### 409 Conflict - Email Already Exists
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 409,
  "path": "/api/auth/register",
  "message": "Email already registered",
  "details": ["CONFLICT"]
}
```

## Default Values

The system automatically sets the following default values for new users:

### User Authentication
- **Username**: Same as email
- **Enabled**: `true`
- **Account Non Expired**: `true`
- **Account Non Locked**: `true`
- **Credentials Non Expired**: `true`
- **Failed Attempts**: `0`
- **Role**: `ROLE_USER`

### User Profile
- **Display Name**: `firstname + " " + lastname`
- **Created At**: Current timestamp
- **Updated At**: Current timestamp

## Security Features

### Password Encryption
- Passwords are encrypted using BCrypt before storage
- Original passwords are never stored in plain text

### Email Uniqueness
- Email addresses must be unique across the system
- Duplicate registration attempts return 409 Conflict

### Input Validation
- All required fields are validated
- Email format validation
- Password length requirements (6-100 characters)
- Name length limits (max 100 characters each)

## Usage Examples

### cURL Example

```bash
curl -X POST http://localhost:8089/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstname": "John",
    "lastname": "Doe",
    "password": "securePassword123"
  }'
```

### JavaScript Example

```javascript
const registerUser = async (userData) => {
  const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData)
  });
  
  if (response.ok) {
    const result = await response.json();
    console.log('Registration successful:', result);
  } else {
    const error = await response.json();
    console.error('Registration failed:', error);
  }
};

// Usage
registerUser({
  email: 'john.doe@example.com',
  firstname: 'John',
  lastname: 'Doe',
  password: 'securePassword123'
});
```

## Integration with Authentication

After successful registration, users can immediately log in using the `/api/auth/login` endpoint with their email and password.

### Login After Registration

```bash
curl -X POST http://localhost:8089/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

## Testing

### Unit Tests
Run controller unit tests:
```bash
mvn test -Dtest=UserRegistrationControllerTest
```

### Integration Tests
Run full integration tests:
```bash
mvn test -Dtest=UserRegistrationIntegrationTest
```

### Test Scenarios Covered
- ✅ Successful registration with valid data
- ✅ Duplicate email handling
- ✅ Input validation (invalid email, missing fields, short password)
- ✅ Automatic role assignment
- ✅ Password encryption
- ✅ Default value assignment

## Configuration

### Constants
Default values are defined in `UserConstants.java`:

```java
public static final Boolean DEFAULT_ENABLED = true;
public static final Boolean DEFAULT_ACCOUNT_NON_EXPIRED = true;
public static final Boolean DEFAULT_ACCOUNT_NON_LOCKED = true;
public static final Boolean DEFAULT_CREDENTIALS_NON_EXPIRED = true;
public static final Integer DEFAULT_FAILED_ATTEMPTS = 0;
public static final String DEFAULT_USER_ROLE = "ROLE_USER";
```

### Validation Rules
- **Email**: Must be valid format, max 255 characters
- **Names**: Required, max 100 characters each
- **Password**: Required, 6-100 characters

## Database Schema

The registration process creates records in:

1. **user_profile** table (parent)
   - Stores profile information (email, names, etc.)
   
2. **users** table (child)
   - Stores authentication credentials
   
3. **user_roles** table (junction)
   - Links user to ROLE_USER

## Error Handling

The API provides comprehensive error handling:

- **Validation Errors**: Detailed field-level validation messages
- **Duplicate Email**: Clear conflict error message
- **System Errors**: Generic error handling for unexpected issues
- **Structured Responses**: Consistent error format across all endpoints

## OpenAPI Documentation

The endpoint is fully documented in Swagger UI at:
- **Local**: http://localhost:8089/swagger-ui.html
- **API Docs**: http://localhost:8089/v3/api-docs

The documentation includes:
- Request/response schemas
- Validation rules
- Example payloads
- Error response formats