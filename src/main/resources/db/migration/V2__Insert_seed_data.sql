-- Set schema path
SET search_path TO myhealth_schema;

-- Insert roles
INSERT INTO roles (id, name, description) VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'ROLE_ADMIN', 'Administrator role with full access'),
('550e8400-e29b-41d4-a716-446655440002', 'ROLE_USER', 'Standard user role with limited access'),
('550e8400-e29b-41d4-a716-446655440005', 'ROLE_SUPER_ADMIN', 'Super administrator with highest privileges'),
('550e8400-e29b-41d4-a716-446655440006', 'ROLE_OWNER', 'Owner role with business management access'),
('550e8400-e29b-41d4-a716-446655440007', 'ROLE_COACH', 'Coach role for health and fitness guidance');

-- Insert user profiles
INSERT INTO user_profile (id, email, first_name, last_name, display_name, phone_number, city, state, country, gender, language_preference, timezone, theme_preference, is_public_profile, created_at, updated_at) VALUES 
('550e8400-e29b-41d4-a716-446655440003', 'admin@myhealth.com', 'Admin', 'User', 'Admin User', '+1234567890', 'New York', 'NY', 'USA', 'Other', 'en', 'America/New_York', 'light', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'user@myhealth.com', 'Regular', 'User', 'Regular User', '+1234567891', 'Los Angeles', 'CA', 'USA', 'Other', 'en', 'America/Los_Angeles', 'light', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440008', 'superadmin@myhealth.com', 'Super', 'Admin', 'Super Admin', '+1234567892', 'Chicago', 'IL', 'USA', 'Other', 'en', 'America/Chicago', 'dark', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert users (password is 'password' encoded with BCrypt)
INSERT INTO users (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired, failed_attempts, created_at, updated_at) VALUES 
('550e8400-e29b-41d4-a716-446655440003', 'admin', '$2a$12$WDXBnJVyz7hX//3koasmneRaddbG6RsEM7l.oxJm0LFKvUTKcjRse', true, true, true, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440004', 'user', '$2a$12$WDXBnJVyz7hX//3koasmneRaddbG6RsEM7l.oxJm0LFKvUTKcjRse', true, true, true, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440008', 'superadmin', '$2a$12$WDXBnJVyz7hX//3koasmneRaddbG6RsEM7l.oxJm0LFKvUTKcjRse', true, true, true, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES 
('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001'), -- admin user gets ADMIN role
('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002'), -- regular user gets USER role
('550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440005'); -- superadmin user gets SUPER_ADMIN role