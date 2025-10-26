-- Create schema
CREATE SCHEMA IF NOT EXISTS myhealth_schema;
SET search_path TO myhealth_schema;

-- ROLES TABLE
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,  -- e.g. ROLE_USER, ROLE_ADMIN
    description VARCHAR(255)
);

-- USER_PROFILE (Parent Table)
CREATE TABLE user_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(150),
    profile_picture_url VARCHAR(512),
    phone_number VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(20),
    bio TEXT,
    website VARCHAR(255),
    language_preference VARCHAR(10),
    timezone VARCHAR(50),
    theme_preference VARCHAR(20),
    is_public_profile BOOLEAN DEFAULT TRUE,
    last_active_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- USERS TABLE (Child Table)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,                -- Store BCrypt-hashed passwords
    enabled BOOLEAN DEFAULT TRUE NOT NULL,
    account_non_expired BOOLEAN DEFAULT TRUE NOT NULL,
    account_non_locked BOOLEAN DEFAULT TRUE NOT NULL,
    credentials_non_expired BOOLEAN DEFAULT TRUE NOT NULL,
    failed_attempts INT DEFAULT 0,
    last_login_at TIMESTAMP,
    last_password_reset_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_users_user_profile FOREIGN KEY (id) REFERENCES user_profile (id) ON DELETE CASCADE
);

-- Many-to-many relationship between users and roles
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_profile (id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- USER_TOKENS TABLE
CREATE TABLE user_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_token_user FOREIGN KEY (user_id) REFERENCES user_profile (id) ON DELETE CASCADE
);

-- INDEXES for performance
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_enabled ON users (enabled);
CREATE INDEX idx_user_profile_email ON user_profile (email);
CREATE INDEX idx_user_tokens_refresh_token ON user_tokens (refresh_token);
CREATE INDEX idx_user_tokens_user_id ON user_tokens (user_id);