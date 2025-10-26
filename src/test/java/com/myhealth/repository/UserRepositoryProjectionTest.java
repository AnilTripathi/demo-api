package com.myhealth.repository;

import com.myhealth.entity.Role;
import com.myhealth.entity.User;
import com.myhealth.entity.UserProfile;
import com.myhealth.entity.UserRole;
import com.myhealth.entity.UserRoleId;
import com.myhealth.projection.UserLoginProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryProjectionTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @BeforeEach
    public void setUp() {
        // Create role
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("Standard user role");
        Role savedRole = roleRepository.save(userRole);
        
        // Create user profile
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("test@example.com");
        userProfile.setFirstName("Test");
        userProfile.setLastName("User");
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        
        // Create user
        User user = new User();
        user.setId(savedProfile.getId());
        user.setUsername("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setFailedAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        // Create user role relationship
        UserRoleId userRoleId = new UserRoleId(savedUser.getId(), savedRole.getId());
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setId(userRoleId);
        userRoleEntity.setUser(savedUser);
        userRoleEntity.setRole(savedRole);
        userRoleRepository.save(userRoleEntity);
    }
    
    @Test
    public void testFindByUsernameForLogin_ReturnsProjection() {
        // When
        Optional<UserLoginProjection> result = userRepository.findByUsernameForLogin("test@example.com");
        
        // Then
        assertTrue(result.isPresent(), "User projection should be found");
        
        UserLoginProjection projection = result.get();
        assertNotNull(projection.getId(), "ID should not be null");
        assertEquals("test@example.com", projection.getUsername(), "Username should match");
        assertNotNull(projection.getPassword(), "Password should not be null");
        assertTrue(projection.getPassword().startsWith("$2a$"), "Password should be BCrypt encoded");
        assertTrue(projection.getEnabled(), "User should be enabled");
        assertTrue(projection.getAccountNonExpired(), "Account should be non-expired");
        assertTrue(projection.getAccountNonLocked(), "Account should be non-locked");
        assertTrue(projection.getCredentialsNonExpired(), "Credentials should be non-expired");
        assertEquals("ROLE_USER", projection.getRoles(), "Roles should contain ROLE_USER");
    }
    
    @Test
    public void testFindByUsernameForLogin_UserNotFound() {
        // When
        Optional<UserLoginProjection> result = userRepository.findByUsernameForLogin("nonexistent@example.com");
        
        // Then
        assertFalse(result.isPresent(), "Non-existent user should not be found");
    }
    
    @Test
    public void testProjectionContainsOnlyRequiredFields() {
        // When
        Optional<UserLoginProjection> result = userRepository.findByUsernameForLogin("test@example.com");
        
        // Then
        assertTrue(result.isPresent());
        UserLoginProjection projection = result.get();
        
        // Verify all required fields are present
        assertNotNull(projection.getId());
        assertNotNull(projection.getUsername());
        assertNotNull(projection.getPassword());
        assertNotNull(projection.getEnabled());
        assertNotNull(projection.getAccountNonExpired());
        assertNotNull(projection.getAccountNonLocked());
        assertNotNull(projection.getCredentialsNonExpired());
        assertNotNull(projection.getRoles());
        
        // Verify projection is not a full entity (should be a proxy)
        assertFalse(projection instanceof User, "Projection should not be a User entity");
    }
}