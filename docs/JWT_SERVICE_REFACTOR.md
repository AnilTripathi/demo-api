# JWT Service Refactor: Centralized User ID Extraction

## Overview

Refactored the JWT authentication system to centralize user ID extraction through a new `getLoggedInUserId()` method in `JwtTokenService`. This eliminates the need for controllers to extract and pass user IDs to service methods.

## Key Changes

### 1. New JwtTokenService Method

Added `getLoggedInUserId()` method that:
- Extracts the logged-in user's UUID from Spring Security's current authentication context
- Serves as the single source of truth for logged-in user identity in the service layer
- Throws `UnauthorizedException` if no valid authentication is present

```java
/**
 * Extracts the logged-in user's UUID from the current security context.
 * This should be the single source of truth for logged-in user identity in the service layer.
 * 
 * @return UUID of the currently authenticated user
 * @throws UnauthorizedException if no authentication is present or invalid
 */
UUID getLoggedInUserId();
```

### 2. Service Layer Refactoring

**Before:**
```java
// Service methods required userId parameter
UserTaskResponse createTask(UUID userId, UserTaskCreateRequest request);

// Controllers extracted userId and passed it
UUID userId = getUserId(authentication);
UserTaskResponse task = userTaskService.createTask(userId, request);
```

**After:**
```java
// Service methods no longer need userId parameter
UserTaskResponse createTask(UserTaskCreateRequest request);

// Services get userId internally
UUID userId = jwtTokenService.getLoggedInUserId();

// Controllers simply call service methods
UserTaskResponse task = userTaskService.createTask(request);
```

### 3. Updated Service Methods

All `UserTaskService` methods now:
- Remove `userId` parameters from method signatures
- Call `jwtTokenService.getLoggedInUserId()` internally
- Maintain the same business logic and security checks

**Updated Methods:**
- `getUserTasks()` - removed userId parameter
- `getUserTask(UUID taskId)` - removed userId parameter  
- `createTask(UserTaskCreateRequest request)` - removed userId parameter
- `updateTask(UUID taskId, UserTaskUpdateRequest request)` - removed userId parameter
- `deleteTask(UUID taskId)` - removed userId parameter
- `changeTaskStatus(UUID taskId, Short statusId)` - removed userId parameter

### 4. Controller Simplification

**Removed from controllers:**
- `Authentication` parameters
- `getUserId()` helper methods
- User ID extraction logic
- User ID passing to service methods

**Example transformation:**
```java
// Before
@PostMapping
public ResponseEntity<UserTaskResponse> createTask(
        @Valid @RequestBody UserTaskCreateRequest request,
        Authentication authentication) {
    UUID userId = getUserId(authentication);
    UserTaskResponse task = userTaskService.createTask(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
}

// After  
@PostMapping
public ResponseEntity<UserTaskResponse> createTask(
        @Valid @RequestBody UserTaskCreateRequest request) {
    UserTaskResponse task = userTaskService.createTask(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
}
```

## Security Considerations

### Enhanced Security
- **Centralized Authentication**: Single point of user ID extraction reduces inconsistencies
- **Context-Based**: Uses Spring Security's authentication context, ensuring proper security flow
- **Fail-Safe**: Throws `UnauthorizedException` for invalid/missing authentication

### Authentication Flow
1. JWT filter validates token and sets `ApiUserDetail` in security context
2. Service methods call `getLoggedInUserId()` when needed
3. Method extracts user ID from `ApiUserDetail` principal
4. Business logic proceeds with authenticated user's ID

## Error Handling

### UnauthorizedException
New exception class for authentication failures:
```java
public class UnauthorizedException extends ApiException {
    public UnauthorizedException() {
        super("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
```

### Error Scenarios
- **No Authentication**: When `SecurityContextHolder` has no authentication
- **Unauthenticated User**: When `authentication.isAuthenticated()` returns false
- **Wrong Principal Type**: When principal is not `ApiUserDetail`

All scenarios return HTTP 401 with JSON: `{"message": "Unauthorized"}`

## Testing

### Unit Tests
- `JwtTokenServiceGetLoggedInUserIdTest`: Tests all authentication scenarios
- `UserTaskServiceJwtIntegrationTest`: Verifies service methods call `getLoggedInUserId()`
- Updated existing service tests to mock `JwtTokenService`

### Test Coverage
- ✅ Valid authentication returns correct user ID
- ✅ No authentication throws `UnauthorizedException`
- ✅ Unauthenticated user throws `UnauthorizedException`  
- ✅ Wrong principal type throws `UnauthorizedException`
- ✅ Service methods call `getLoggedInUserId()` internally

## Benefits

### Code Quality
- **Reduced Duplication**: No more user ID extraction in every controller
- **Single Responsibility**: Controllers focus on HTTP concerns, services handle business logic
- **Consistency**: All services use the same method for user identification

### Maintainability  
- **Centralized Logic**: Authentication changes only affect one method
- **Cleaner APIs**: Service interfaces are simpler without userId parameters
- **Easier Testing**: Mock one method instead of authentication in every test

### Security
- **Reduced Attack Surface**: Less places where user ID extraction can go wrong
- **Context Validation**: Ensures proper Spring Security authentication flow
- **Type Safety**: Guarantees `ApiUserDetail` principal type

## Migration Impact

### Breaking Changes
- Service method signatures changed (removed userId parameters)
- Controller method signatures simplified
- Tests need to mock `JwtTokenService.getLoggedInUserId()`

### Backward Compatibility
- No API endpoint changes for clients
- Same authentication flow and token format
- Same authorization and business logic

## Performance Impact

- **Minimal Overhead**: Single method call per service operation
- **Context Reuse**: Leverages existing Spring Security context
- **No Additional Queries**: Uses in-memory authentication data

## Future Considerations

### Potential Enhancements
- Cache user details in security context to avoid repeated lookups
- Add audit logging in `getLoggedInUserId()` for security monitoring
- Consider user impersonation support for admin features

### Extension Points
- Method can be extended to return full `ApiUserDetail` if needed
- Additional validation logic can be added centrally
- Support for service-to-service authentication tokens