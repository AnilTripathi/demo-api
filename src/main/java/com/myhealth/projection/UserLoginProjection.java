package com.myhealth.projection;

import java.util.UUID;

/**
 * Projection interface for optimized user login data retrieval.
 * Contains only the essential fields required for authentication,
 * avoiding the overhead of loading full User and UserProfile entities.
 */
public interface UserLoginProjection {
    
    /**
     * @return User ID (UUID)
     */
    UUID getId();
    
    /**
     * @return Username (email) used for authentication
     */
    String getUsername();
    
    /**
     * @return BCrypt encoded password hash
     */
    String getPassword();
    
    /**
     * @return Whether the account is enabled
     */
    Boolean getEnabled();
    
    /**
     * @return Whether the account is non-expired
     */
    Boolean getAccountNonExpired();
    
    /**
     * @return Whether the account is non-locked
     */
    Boolean getAccountNonLocked();
    
    /**
     * @return Whether the credentials are non-expired
     */
    Boolean getCredentialsNonExpired();
    
    /**
     * @return Comma-separated list of role names (e.g., "ROLE_USER,ROLE_ADMIN")
     */
    String getRoles();
}