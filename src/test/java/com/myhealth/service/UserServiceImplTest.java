package com.myhealth.service;

import com.myhealth.dto.UserInfo;
import com.myhealth.entity.UserProfile;
import com.myhealth.impl.UserServiceImpl;
import com.myhealth.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserProfileRepository userProfileRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private UserProfile userProfile1;
    private UserProfile userProfile2;
    
    @BeforeEach
    void setUp() {
        userProfile1 = new UserProfile();
        userProfile1.setId(UUID.randomUUID());
        userProfile1.setFirstName("John");
        userProfile1.setLastName("Doe");
        userProfile1.setEmail("john.doe@example.com");
        userProfile1.setProfilePictureUrl("https://example.com/john.jpg");
        
        userProfile2 = new UserProfile();
        userProfile2.setId(UUID.randomUUID());
        userProfile2.setFirstName("Jane");
        userProfile2.setLastName("Smith");
        userProfile2.setEmail("jane.smith@example.com");
        userProfile2.setProfilePictureUrl("https://example.com/jane.jpg");
    }
    
    @Test
    void getAllUsers_ShouldReturnListOfUserInfo() {
        // Given
        List<UserProfile> userProfiles = Arrays.asList(userProfile1, userProfile2);
        when(userProfileRepository.findAll()).thenReturn(userProfiles);
        
        // When
        List<UserInfo> result = userService.getAllUsers();
        
        // Then
        assertThat(result).hasSize(2);
        
        UserInfo userInfo1 = result.get(0);
        assertThat(userInfo1.getId()).isEqualTo(userProfile1.getId());
        assertThat(userInfo1.getFirstName()).isEqualTo("John");
        assertThat(userInfo1.getLastName()).isEqualTo("Doe");
        assertThat(userInfo1.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(userInfo1.getPic()).isEqualTo("https://example.com/john.jpg");
        
        UserInfo userInfo2 = result.get(1);
        assertThat(userInfo2.getId()).isEqualTo(userProfile2.getId());
        assertThat(userInfo2.getFirstName()).isEqualTo("Jane");
        assertThat(userInfo2.getLastName()).isEqualTo("Smith");
        assertThat(userInfo2.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(userInfo2.getPic()).isEqualTo("https://example.com/jane.jpg");
    }
    
    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Given
        when(userProfileRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        List<UserInfo> result = userService.getAllUsers();
        
        // Then
        assertThat(result).isEmpty();
    }
}