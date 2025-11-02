# Admin User Profile Management API

## Overview
This implementation provides admin-only REST APIs for creating and updating user profiles with comprehensive validation, security, and clean layering.

## Endpoints

### POST /api/admin/user
- **Purpose**: Create a new user profile
- **Access**: Admin only (roles containing "ADMIN")
- **Request**: `UserProfileCreateRequest` with validation
- **Response**: 201 Created with `UserProfileResponse`
- **Errors**: 400 (validation), 401 (unauthorized), 403 (forbidden), 409 (duplicate email)

### PUT /api/admin/user/{id}
- **Purpose**: Update existing user profile by UUID
- **Access**: Admin only (roles containing "ADMIN")
- **Request**: `UserProfileUpdateRequest` with validation
- **Response**: 200 OK with updated `UserProfileResponse`
- **Errors**: 400 (validation), 401 (unauthorized), 403 (forbidden), 404 (not found), 409 (duplicate email)

## Components Created

### DTOs
- `UserProfileCreateRequest` - Create request with comprehensive validation
- `UserProfileUpdateRequest` - Update request with same validation rules
- `UserProfileResponse` - Response DTO with all profile fields including timestamps

### Validation Rules
- **Email**: @NotBlank, @Email, max 255 chars, unique constraint
- **First/Last Name**: @NotBlank, 1-100 chars
- **Gender**: Required, enum pattern (Male|Female|Other|PreferNotToSay)
- **Date of Birth**: @Past validation, LocalDate format
- **URLs**: @URL validation for website and profile picture
- **Phone**: Pattern validation for international format
- **Postal Code**: Pattern validation
- **Theme**: Pattern validation (light|dark|auto)

### Repository
- `UserProfileRepository` extends JpaRepository
- Methods: findByEmail, existsByEmail, existsByEmailAndIdNot
- Supports uniqueness checks for create/update operations

### Service Layer
- `UserProfileService` interface with create/update methods
- `UserProfileServiceImpl` with @Transactional boundaries
- Business logic: email uniqueness validation, entity mapping
- Exception handling: DuplicateResourceException, ResourceNotFoundException

### Mapping
- `UserProfileMapper` component for DTO ↔ Entity conversion
- String trimming and null handling
- Separate methods for create, update, and response mapping

### Controller
- `UserProfileController` with OpenAPI annotations
- Proper HTTP status codes (201 for create, 200 for update)
- Comprehensive API documentation with Swagger annotations
- Clean separation of concerns - thin controller layer

### Exception Handling
- `DuplicateResourceException` for email conflicts (409 Conflict)
- `ResourceNotFoundException` for missing profiles (404 Not Found)
- Enhanced `GlobalExceptionHandler` with proper error responses
- Structured JSON error responses with error codes

### Security
- Admin-only access enforced by existing SecurityConfig
- `/api/admin/**` pattern requires roles containing "ADMIN"
- Proper 401/403 responses for unauthorized/forbidden access

### Tests
- `UserProfileControllerTest` - Unit tests with mocked service
- `UserProfileIntegrationTest` - End-to-end integration tests
- Coverage: happy paths, validation errors, security, duplicate handling
- Test scenarios: create/update success, validation failures, access control

## Database Schema
The existing `UserProfile` entity maps to `user_profile` table with:
- UUID primary key with auto-generation
- Email unique constraint
- Proper column mappings and lengths
- Audit fields (createdAt, updatedAt) with @PrePersist/@PreUpdate

## Key Features
1. **Comprehensive Validation**: Field-level validation with meaningful error messages
2. **Email Uniqueness**: Enforced at both database and service level
3. **Clean Architecture**: Controller → Service → Repository → Entity layering
4. **Proper Error Handling**: Structured JSON responses with appropriate HTTP status codes
5. **Security**: Admin-only access with existing role-based authorization
6. **Documentation**: OpenAPI/Swagger annotations for API documentation
7. **Testing**: Unit and integration tests covering all scenarios
8. **Data Integrity**: Transactional boundaries and defensive programming

## Usage Examples

### Create User Profile
```bash
POST /api/admin/user
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "dateOfBirth": "1990-05-15",
  "phoneNumber": "+1-555-123-4567",
  "city": "New York",
  "country": "USA"
}
```

### Update User Profile
```bash
PUT /api/admin/user/c1a5b8b2-3fd6-4bd3-9d73-8dfd9f0b12a1
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Doe Updated",
  "gender": "Male"
}
```

## Files Created
- DTOs: `UserProfileCreateRequest`, `UserProfileUpdateRequest`, `UserProfileResponse`
- Repository: `UserProfileRepository`
- Service: `UserProfileService`, `UserProfileServiceImpl`
- Controller: `UserProfileController`
- Mapper: `UserProfileMapper`
- Exceptions: `DuplicateResourceException`, `ResourceNotFoundException`
- Tests: `UserProfileControllerTest`, `UserProfileIntegrationTest`
- Enhanced: `GlobalExceptionHandler`

All components follow Spring Boot best practices with proper dependency injection, validation, error handling, and testing coverage.