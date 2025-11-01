# JWT Authentication Flow Update

## Overview

The JWT authentication flow has been updated to use `userId` as the JWT subject instead of username, introduce a custom `ApiUserDetail` class, and modify the refresh token endpoint to accept expired access tokens.

## Key Changes

### 1. JWT Subject Change
- **Before**: JWT `sub` (subject) contained the username
- **After**: JWT `sub` (subject) contains the `userId` (UUID)
- **Impact**: All token validation and user identification now uses UUID instead of username

### 2. ApiUserDetail Class
- New `ApiUserDetail` class implements `UserDetails` interface
- Contains both `id` (UUID) and `username` fields
- Used throughout the application for security principal
- Replaces standard Spring Security `UserDetails` implementation

### 3. Refresh Token Flow
- **New Behavior**: Accepts expired access tokens for refresh operations
- **Validation**: Only validates token signature, not expiration for access tokens
- **Subject Matching**: Verifies `userId` from access token matches stored `user_token` record
- **Refresh Token Expiry**: Only refresh token expiration causes authentication failure

## API Changes

### Refresh Endpoint Request
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...", // Can be expired
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Error Responses
All authentication errors return consistent JSON format:

```json
{
  "message": "Error description"
}
```

#### Specific Error Messages:
- `"Invalid token signature"` - Access token signature validation failed
- `"Token subject mismatch"` - UserId in access token doesn't match user_token record
- `"Refresh token expired, please login again"` - Refresh token has expired
- `"Token expired"` - Access token expired on protected endpoints

## Implementation Details

### Token Generation
```java
// New method signature
String generateAccessToken(UUID userId, Collection<? extends GrantedAuthority> authorities)

// JWT Claims
{
  "sub": "123e4567-e89b-12d3-a456-426614174000", // userId as string
  "authorities": ["ROLE_USER"],
  "iat": 1640995200,
  "exp": 1640996100
}
```

### Principal Access in Controllers
```java
@GetMapping("/profile")
public ResponseEntity<UserInfo> getProfile(@AuthenticationPrincipal ApiUserDetail userDetail) {
    UUID userId = userDetail.getId();
    String username = userDetail.getUsername();
    // ... rest of implementation
}
```

### Refresh Flow Logic
1. Validate access token signature (ignore expiration)
2. Extract `userId` from access token subject
3. Find `user_token` record by refresh token
4. Verify `userId` matches `user_token.userId`
5. Check refresh token expiration
6. Generate new tokens if all validations pass

## Migration Notes

### Backward Compatibility
- Existing tokens with username as subject are not supported
- All users must re-authenticate after deployment
- Consider implementing a grace period for token migration if needed

### Database Impact
- No schema changes required
- `user_token` table already stores `userId` for validation

## Testing

### Unit Tests
- Token generation with `userId` as subject
- Signature-only validation methods
- Claims parsing with expired tokens

### Integration Tests
- Refresh with expired access token + valid refresh token → Success
- Invalid signature → 401 "Invalid token signature"
- Subject mismatch → 401 "Token subject mismatch"  
- Expired refresh token → 401 "Refresh token expired, please login again"
- Protected endpoint with expired token → 401 "Token expired"

## Security Considerations

### Enhanced Security
- User identification by UUID prevents username enumeration
- Signature validation ensures token integrity even when expired
- Subject matching prevents token reuse across different users

### Potential Risks
- Expired access tokens can still be used for refresh (by design)
- Ensure refresh token rotation is properly implemented
- Monitor for unusual refresh patterns

## Deployment Checklist

- [ ] Update client applications to include `accessToken` in refresh requests
- [ ] Update API documentation with new request/response formats
- [ ] Inform users about required re-authentication
- [ ] Monitor error logs for authentication issues
- [ ] Verify all controllers using `@AuthenticationPrincipal` are updated

## Performance Impact

- Minimal performance impact
- Additional database lookup in JWT filter to get user by ID
- Consider caching user lookups if performance becomes an issue