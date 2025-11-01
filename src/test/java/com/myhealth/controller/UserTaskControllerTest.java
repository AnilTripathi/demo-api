package com.myhealth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.dto.task.UserTaskResponse;
import com.myhealth.dto.task.UserTaskStatusChangeRequest;
import com.myhealth.service.UserTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTaskController.class)
class UserTaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserTaskService userTaskService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void getUserTasks_ShouldReturnPagedResults() throws Exception {
        // Given
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UserTaskResponse task = new UserTaskResponse();
        task.setId(UUID.randomUUID());
        task.setTitle("Test Task");
        
        Page<UserTaskResponse> page = new PageImpl<>(List.of(task));
        when(userTaskService.getUserTasks(eq(userId), any(), any(), any(), any(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/user/task"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }
    
    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void createTask_ShouldReturnCreatedTask() throws Exception {
        // Given
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UserTaskCreateRequest request = new UserTaskCreateRequest();
        request.setTitle("New Task");
        request.setPriorityId("3");
        
        UserTaskResponse response = new UserTaskResponse();
        response.setId(UUID.randomUUID());
        response.setTitle("New Task");
        
        when(userTaskService.createTask(eq(userId), any(UserTaskCreateRequest.class)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/user/task")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("New Task"));
    }
    
    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UserTaskCreateRequest request = new UserTaskCreateRequest();
        // Missing required title
        
        // When & Then
        mockMvc.perform(post("/api/user/task")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void deleteTask_ShouldReturnNoContent() throws Exception {
        // Given
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID taskId = UUID.randomUUID();
        
        doNothing().when(userTaskService).deleteTask(userId, taskId);
        
        // When & Then
        mockMvc.perform(delete("/api/user/task/{id}", taskId)
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void changeTaskStatus_ShouldReturnUpdatedTask() throws Exception {
        // Given
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID taskId = UUID.randomUUID();
        
        UserTaskStatusChangeRequest request = new UserTaskStatusChangeRequest();
        request.setStatusId("5");
        
        UserTaskResponse response = new UserTaskResponse();
        response.setId(taskId);
        response.setStatusId((short) 5);
        response.setCompletedAt(ZonedDateTime.now());
        
        when(userTaskService.changeTaskStatus(eq(userId), eq(taskId), eq((short) 5)))
            .thenReturn(response);
        
        // When & Then
        mockMvc.perform(patch("/api/user/task/{id}/status", taskId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.statusId").value(5))
            .andExpect(jsonPath("$.completedAt").exists());
    }
    
    @Test
    void getUserTasks_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/task"))
            .andExpect(status().isUnauthorized());
    }
}