# Refresh Token Implementation - Complete Guide

## Overview

The refresh token endpoint (`POST /api/auth/refresh`) has been implemented to handle expired access tokens correctly while maintaining security. The implementation follows the requirements to accept expired access tokens as long as their signature is valid, then validate the refresh token's expiry and state.

## Key Features

### 1. Signature-Only Validation for Access Tokens
- ✅ **Accepts expired access tokens** with valid signatures
- ✅ **Rejects invalid signatures** immediately
- ✅ **Extracts userId** from expired tokens using `parseClaimsIgnoreExpiration()`
- ✅ **Validates signature only** using `validateTokenSignature()`

### 2. Comprehensive Refresh Token Validation
- ✅ **Checks refresh token existence** in database
- ✅ **Validates expiry** - returns specific error for expired refresh tokens
- ✅ **Subject matching** - ensures access token userId matches refresh token userId
- ✅ **Token rotation** - generates new refresh token and revokes old one

### 3. Proper Error Handling
- ✅ **Consistent JSON responses** with meaningful error messages
- ✅ **Appropriate HTTP status codes** (401 for authentication failures)
- ✅ **No sensitive information leakage** in error messages

## Implementation Details

### JWT Token Service Enhancements

#### `validateTokenSignature(String token)`
```java
@Override
public boolean validateTokenSignature(String token) {
    if (token == null || token.trim().isEmpty()) {
        return false;
    }
    try {
        parseClaimsIgnoreExpiration(token);
        return true;
    } catch (JwtException e) {
        return false;
    }
}
```

#### `parseClaimsIgnoreExpiration(String token)`
```java
@Override
public Claims parseClaimsIgnoreExpiration(String token) {
    if (token == null || token.trim().isEmpty()) {
        throw new JwtException("Token cannot be null or empty");
    }
    try {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    } catch (ExpiredJwtException e) {
        return e.getClaims(); // Return claims even if expired
    }
}
```

### Refresh Endpoint Logic

The `AuthServiceImpl.refresh()` method implements the complete flow:

1. **Validate Access Token Signature**
   ```java
   if (!jwtTokenService.validateTokenSignature(refreshRequest.getAccessToken())) {
       throw new ApiException("Invalid token signature", HttpStatus.UNAUTHORIZED);
   }
   ```

2. **Extract User ID from Access Token**
   ```java
   Claims claims = jwtTokenService.parseClaimsIgnoreExpiration(refreshRequest.getAccessToken());
   UUID userId = UUID.fromString(claims.getSubject());
   ```

3. **Validate Refresh Token**
   ```java
   UserToken userToken = userTokenRepository.findByRefreshToken(refreshRequest.getRefreshToken())
           .orElseThrow(() -> new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED));
   ```

4. **Check Subject Matching**
   ```java
   if (!userId.equals(userToken.getUserId())) {
       throw new ApiException("Token subject mismatch", HttpStatus.UNAUTHORIZED);
   }
   ```

5. **Check Refresh Token Expiry**
   ```java
   if (userToken.isExpired()) {
       userTokenRepository.delete(userToken);
       throw new ApiException("Refresh token expired, please login again", HttpStatus.UNAUTHORIZED);
   }
   ```

6. **Generate New Tokens**
   ```java
   String newAccessToken = jwtTokenService.generateAccessToken(userDetails.getId(), userDetails.getAuthorities());
   String newRefreshToken = jwtTokenService.generateRefreshToken(user.getId());
   jwtTokenService.revokeRefreshToken(refreshRequest.getRefreshToken());
   ```

## Error Responses

### Invalid Token Signature
```json
{
  "message": "Invalid token signature"
}
```
**Status**: 401 Unauthorized

### Expired Refresh Token
```json
{
  "message": "Refresh token expired, please login again"
}
```
**Status**: 401 Unauthorized

### Token Subject Mismatch
```json
{
  "message": "Token subject mismatch"
}
```
**Status**: 401 Unauthorized

### Invalid Refresh Token
```json
{
  "message": "Invalid refresh token"
}
```
**Status**: 401 Unauthorized

## Security Considerations

### 1. Access Token Handling
- **Signature validation only** - ignores expiration for refresh flow
- **Subject extraction** - uses userId (UUID) not username
- **No full validation** - prevents "Token expired" errors during refresh

### 2. Refresh Token Security
- **Database storage** - refresh tokens stored securely in database
- **Expiry checking** - validates refresh token hasn't expired
- **Token rotation** - old refresh token is revoked when new one is issued
- **Subject matching** - prevents token hijacking across users

### 3. Normal Request Behavior
- **Full validation** - regular endpoints still enforce access token expiration
- **Consistent behavior** - only refresh endpoint has special handling
- **JWT filter unchanged** - authentication filter still rejects expired tokens

## Testing Coverage

### Edge Cases Covered
1. ✅ **Valid expired access token + valid refresh token** → Success
2. ✅ **Invalid access token signature** → "Invalid token signature"
3. ✅ **Valid signature + expired refresh token** → "Refresh token expired, please login again"
4. ✅ **Subject mismatch** → "Token subject mismatch"
5. ✅ **Non-existent refresh token** → "Invalid refresh token"
6. ✅ **Malformed access token** → "Invalid token signature"
7. ✅ **Empty/null access token** → "Invalid token signature"

### Test Files
- `RefreshTokenIntegrationTest.java` - Basic refresh token functionality
- `RefreshTokenEdgeCasesTest.java` - Comprehensive edge case testing

## Usage Examples

### Successful Refresh
```bash
curl -X POST http://localhost:8089/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...", 
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "660f9500-f39c-52e5-b827-557766551111",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Failed Refresh (Expired Refresh Token)
```bash
curl -X POST http://localhost:8089/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...", 
    "refreshToken": "expired-refresh-token"
  }'
```

**Response** (401 Unauthorized):
```json
{
  "message": "Refresh token expired, please login again"
}
```

## Compliance with Requirements

### ✅ Refresh Logic (Signature-Only for Access Token)
- Parse/verify access token signature only (ignore `exp`)
- Extract subject (userId) from access token
- Do not reject on access-token expiration during refresh
- Only reject if signature is invalid

### ✅ Validate Against Persistent Token Store
- Look up `user_token` record for userId
- Ensure refresh token exists, matches, is not expired, and belongs to same userId
- Return 401 with specific messages for different failure scenarios

### ✅ Issue New Tokens
- Generate new access token with subject = userId
- Rotate refresh token and update database
- Return new tokens in response

### ✅ Normal Request Behavior Unchanged
- Non-refresh endpoints still enforce full validation (signature + expiration)
- Expired access tokens still yield 401 "Token expired" for regular endpoints

### ✅ Security Principal & Utilities
- Use custom `ApiUserDetail` with `id` and `username` consistently
- JWT utility has parsing mode that skips expiration checks for refresh flow
- Subject is `userId` (UUID) throughout the system

### ✅ Error Handling (Consistent JSON)
- All errors return JSON body with `message` field
- No sensitive details leaked in error messages

### ✅ Comprehensive Testing
- All specified test scenarios implemented and passing
- Edge cases covered with appropriate error responses

## Conclusion

The refresh token implementation is **fully compliant** with all requirements and provides a secure, robust solution for handling token refresh scenarios. The implementation correctly handles expired access tokens while maintaining security through proper refresh token validation and rotation.