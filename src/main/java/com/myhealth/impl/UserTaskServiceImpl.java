package com.myhealth.impl;

import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.dto.task.UserTaskResponse;
import com.myhealth.dto.task.UserTaskUpdateRequest;
import com.myhealth.entity.task.Priority;
import com.myhealth.entity.task.Status;
import com.myhealth.entity.task.Task;
import com.myhealth.projection.task.UserTaskDetailProjection;
import com.myhealth.projection.task.UserTaskListProjection;
import com.myhealth.repository.PriorityRepository;
import com.myhealth.repository.StatusRepository;
import com.myhealth.repository.TaskRepository;
import com.myhealth.service.JwtTokenService;
import com.myhealth.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserTaskServiceImpl implements UserTaskService {
    
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final PriorityRepository priorityRepository;
    private final JwtTokenService jwtTokenService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserTaskResponse> getUserTasks(Short statusId, String query, 
                                              ZonedDateTime fromDue, ZonedDateTime toDue, Pageable pageable) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Fetching tasks for user: {} with filters - status: {}, query: {}", userId, statusId, query);
        
        Page<UserTaskListProjection> projections = taskRepository.findUserTasksWithFilters(
            userId, statusId, query, fromDue, toDue, pageable);
        
        return projections.map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserTaskResponse getUserTask(UUID taskId) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Fetching task: {} for user: {}", taskId, userId);
        
        UserTaskDetailProjection projection = taskRepository.findUserTaskDetail(taskId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        return mapDetailToResponse(projection);
    }
    
    @Override
    public UserTaskResponse createTask(UserTaskCreateRequest request) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Creating task for user: {} with title: {}", userId, request.getTitle());
        
        Status status = statusRepository.findById((short) 2) // Default to "Todo"
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default status not found"));
        
        Priority priority = priorityRepository.findById(Short.parseShort(request.getPriorityId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid priority"));
        
        Task task = new Task();
        task.setAccountId(userId);
        task.setTitle(request.getTitle());
        task.setDescriptionMd(request.getDescriptionMd());
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueAt(request.getDueAt());
        task.setEstimateMinutes(request.getEstimateMinutes());
        task.setExtras(new HashMap<>());
        
        Task savedTask = taskRepository.save(task);
        log.info("Created task: {} for user: {}", savedTask.getId(), userId);
        
        return getUserTask(savedTask.getId());
    }
    
    @Override
    public UserTaskResponse updateTask(UUID taskId, UserTaskUpdateRequest request) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Updating task: {} for user: {}", taskId, userId);
        
        Task task = taskRepository.findByIdAndAccountId(taskId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        Priority priority = priorityRepository.findById(Short.parseShort(request.getPriorityId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid priority"));
        
        task.setTitle(request.getTitle());
        task.setDescriptionMd(request.getDescriptionMd());
        task.setPriority(priority);
        task.setDueAt(request.getDueAt());
        task.setEstimateMinutes(request.getEstimateMinutes());
        
        taskRepository.save(task);
        log.info("Updated task: {} for user: {}", taskId, userId);
        
        return getUserTask(taskId);
    }
    
    @Override
    public void deleteTask(UUID taskId) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Deleting task: {} for user: {}", taskId, userId);
        
        int deleted = taskRepository.softDeleteTask(taskId, userId, ZonedDateTime.now());
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        
        log.info("Deleted task: {} for user: {}", taskId, userId);
    }
    
    @Override
    public UserTaskResponse changeTaskStatus(UUID taskId, Short statusId) {
        UUID userId = jwtTokenService.getLoggedInUserId();
        log.info("Changing status of task: {} to: {} for user: {}", taskId, statusId, userId);
        
        Task task = taskRepository.findByIdAndAccountId(taskId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        Status newStatus = statusRepository.findById(statusId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status"));
        
        // Validate status transition
        validateStatusTransition(task.getStatus().getId(), statusId);
        
        task.setStatus(newStatus);
        
        // Update completedAt based on status
        if (statusId == 5) { // Done
            task.setCompletedAt(ZonedDateTime.now());
        } else if (task.getCompletedAt() != null) {
            task.setCompletedAt(null);
        }
        
        taskRepository.save(task);
        log.info("Changed status of task: {} to: {} for user: {}", taskId, statusId, userId);
        
        return getUserTask(taskId);
    }
    
    private void validateStatusTransition(Short currentStatus, Short newStatus) {
        // Allow any transition for now, but could add business rules here
        // Example: Cannot go from Done (5) to Backlog (1) directly
        if (currentStatus.equals((short) 5) && newStatus.equals((short) 1)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot move from Done to Backlog directly");
        }
    }
    
    private UserTaskResponse mapToResponse(UserTaskListProjection projection) {
        UserTaskResponse response = new UserTaskResponse();
        response.setId(projection.getId());
        response.setTitle(projection.getTitle());
        response.setDescriptionMd(projection.getDescriptionMd());
        response.setStatusId(projection.getStatusId());
        response.setStatusName(projection.getStatusName());
        response.setPriorityId(projection.getPriorityId());
        response.setPriorityName(projection.getPriorityName());
        response.setDueAt(convertToZonedDateTime(projection.getDueAt()));
        response.setEstimateMinutes(projection.getEstimateMinutes());
        response.setCreatedAt(convertToZonedDateTime(projection.getCreatedAt()));
        response.setUpdatedAt(convertToZonedDateTime(projection.getUpdatedAt()));
        return response;
    }
    
    private UserTaskResponse mapDetailToResponse(UserTaskDetailProjection projection) {
        UserTaskResponse response = new UserTaskResponse();
        response.setId(projection.getId());
        response.setTitle(projection.getTitle());
        response.setDescriptionMd(projection.getDescriptionMd());
        response.setStatusId(projection.getStatusId());
        response.setStatusName(projection.getStatusName());
        response.setPriorityId(projection.getPriorityId());
        response.setPriorityName(projection.getPriorityName());
        response.setDueAt(convertToZonedDateTime(projection.getDueAt()));
        response.setEstimateMinutes(projection.getEstimateMinutes());
        response.setSpentMinutes(projection.getSpentMinutes());
        response.setCompletedAt(convertToZonedDateTime(projection.getCompletedAt()));
        response.setCreatedAt(convertToZonedDateTime(projection.getCreatedAt()));
        response.setUpdatedAt(convertToZonedDateTime(projection.getUpdatedAt()));
        return response;
    }
    
    private ZonedDateTime convertToZonedDateTime(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()) : null;
    }
}