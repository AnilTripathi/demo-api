package com.myhealth.service;

import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.dto.task.UserTaskResponse;
import com.myhealth.dto.task.UserTaskUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface UserTaskService {
    
    Page<UserTaskResponse> getUserTasks(Short statusId, String query, 
                                       ZonedDateTime fromDue, ZonedDateTime toDue, Pageable pageable);
    
    UserTaskResponse getUserTask(UUID taskId);
    
    UserTaskResponse createTask(UserTaskCreateRequest request);
    
    UserTaskResponse updateTask(UUID taskId, UserTaskUpdateRequest request);
    
    void deleteTask(UUID taskId);
    
    UserTaskResponse changeTaskStatus(UUID taskId, Short statusId);
}