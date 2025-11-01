package com.myhealth.config;

import com.myhealth.security.ApiUserDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "springdoc.info.license.name=MIT",
    "springdoc.info.license.url=https://opensource.org/licenses/MIT"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContainsRoleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // Test /api/user/** endpoints

    @Test
    void testUserEndpointWithRoleUser() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "testuser", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserEndpointWithRoleAdmin() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "admin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserEndpointWithRoleSuperAdmin() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "superadmin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserEndpointWithCustomUserRole() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "customuser", 
            "password", 
            List.of(new SimpleGrantedAuthority("USER_MANAGER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserEndpointWithCustomAdminRole() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "customadmin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ADMIN_VIEWER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isOk());
    }

    @Test
    void testUserEndpointWithRoleCoach() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "coach", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_COACH")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserEndpointWithRoleOwner() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "owner", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_OWNER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/user/profile").with(user(userDetail)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserEndpointUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    // Test /api/admin/** endpoints

    @Test
    void testAdminEndpointWithRoleUser() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "testuser", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/admin/users").with(user(userDetail)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminEndpointWithRoleAdmin() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "admin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/admin/users").with(user(userDetail)))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, but access is allowed
    }

    @Test
    void testAdminEndpointWithRoleSuperAdmin() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "superadmin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/admin/users").with(user(userDetail)))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, but access is allowed
    }

    @Test
    void testAdminEndpointWithCustomAdminRole() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "customadmin", 
            "password", 
            List.of(new SimpleGrantedAuthority("ADMIN_MANAGER")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/admin/users").with(user(userDetail)))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, but access is allowed
    }

    @Test
    void testAdminEndpointWithRoleCoach() throws Exception {
        ApiUserDetail userDetail = new ApiUserDetail(
            UUID.randomUUID(), 
            "coach", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_COACH")),
            true, true, true, true
        );
        
        mockMvc.perform(get("/api/admin/users").with(user(userDetail)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminEndpointUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }
}