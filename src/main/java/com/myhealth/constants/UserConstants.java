package com.myhealth.constants;

public final class UserConstants {
    
    // Default user status values
    public static final Boolean DEFAULT_ENABLED = true;
    public static final Boolean DEFAULT_ACCOUNT_NON_EXPIRED = true;
    public static final Boolean DEFAULT_ACCOUNT_NON_LOCKED = true;
    public static final Boolean DEFAULT_CREDENTIALS_NON_EXPIRED = true;
    public static final Integer DEFAULT_FAILED_ATTEMPTS = 0;
    
    // Default role
    public static final String DEFAULT_USER_ROLE = "ROLE_USER";
    
    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_NAME_LENGTH = 100;
    
    private UserConstants() {
        // Prevent instantiation
    }
}