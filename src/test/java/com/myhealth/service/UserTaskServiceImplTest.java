package com.myhealth.service;

import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.dto.task.UserTaskResponse;
import com.myhealth.dto.task.UserTaskUpdateRequest;
import com.myhealth.entity.task.Priority;
import com.myhealth.entity.task.Status;
import com.myhealth.entity.task.Task;
import com.myhealth.impl.UserTaskServiceImpl;
import com.myhealth.projection.task.UserTaskDetailProjection;
import com.myhealth.projection.task.UserTaskListProjection;
import com.myhealth.repository.PriorityRepository;
import com.myhealth.repository.StatusRepository;
import com.myhealth.repository.TaskRepository;
import com.myhealth.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTaskServiceImplTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private StatusRepository statusRepository;
    
    @Mock
    private PriorityRepository priorityRepository;
    
    @Mock
    private JwtTokenService jwtTokenService;
    
    @InjectMocks
    private UserTaskServiceImpl userTaskService;
    
    private UUID userId;
    private UUID taskId;
    private Status todoStatus;
    private Priority mediumPriority;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        
        todoStatus = new Status();
        todoStatus.setId((short) 2);
        todoStatus.setName("Todo");
        todoStatus.setIsDone(false);
        
        mediumPriority = new Priority();
        mediumPriority.setId((short) 3);
        mediumPriority.setName("Medium");
        
        // Mock JwtTokenService to return userId
        when(jwtTokenService.getLoggedInUserId()).thenReturn(userId);
    }
    
    @Test
    void getUserTasks_ShouldReturnPagedResults() {
        // Given
        UserTaskListProjection projection = mock(UserTaskListProjection.class);
        when(projection.getId()).thenReturn(taskId);
        when(projection.getTitle()).thenReturn("Test Task");
        when(projection.getStatusId()).thenReturn((short) 2);
        when(projection.getStatusName()).thenReturn("Todo");
        
        Page<UserTaskListProjection> projectionPage = new PageImpl<>(List.of(projection));
        Pageable pageable = PageRequest.of(0, 20);
        
        when(taskRepository.findUserTasksWithFilters(eq(userId), any(), any(), any(), any(), eq(pageable)))
            .thenReturn(projectionPage);
        
        // When
        Page<UserTaskResponse> result = userTaskService.getUserTasks(null, null, null, null, pageable);
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(taskId);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");
    }
    
    @Test
    void createTask_ShouldCreateAndReturnTask() {
        // Given
        UserTaskCreateRequest request = new UserTaskCreateRequest();
        request.setTitle("New Task");
        request.setPriorityId("3");
        
        Task savedTask = new Task();
        savedTask.setId(taskId);
        
        UserTaskDetailProjection detailProjection = mock(UserTaskDetailProjection.class);
        when(detailProjection.getId()).thenReturn(taskId);
        when(detailProjection.getTitle()).thenReturn("New Task");
        
        when(statusRepository.findById((short) 2)).thenReturn(Optional.of(todoStatus));
        when(priorityRepository.findById((short) 3)).thenReturn(Optional.of(mediumPriority));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskRepository.findUserTaskDetail(taskId, userId)).thenReturn(Optional.of(detailProjection));
        
        // When
        UserTaskResponse result = userTaskService.createTask(request);
        
        // Then
        assertThat(result.getId()).isEqualTo(taskId);
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void getUserTask_WhenTaskNotFound_ShouldThrowException() {
        // Given
        when(taskRepository.findUserTaskDetail(taskId, userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userTaskService.getUserTask(taskId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Task not found");
    }
    
    @Test
    void changeTaskStatus_ShouldUpdateStatusAndCompletedAt() {
        // Given
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(todoStatus);
        
        Status doneStatus = new Status();
        doneStatus.setId((short) 5);
        doneStatus.setName("Done");
        doneStatus.setIsDone(true);
        
        UserTaskDetailProjection detailProjection = mock(UserTaskDetailProjection.class);
        when(detailProjection.getId()).thenReturn(taskId);
        
        when(taskRepository.findByIdAndAccountId(taskId, userId)).thenReturn(Optional.of(task));
        when(statusRepository.findById((short) 5)).thenReturn(Optional.of(doneStatus));
        when(taskRepository.findUserTaskDetail(taskId, userId)).thenReturn(Optional.of(detailProjection));
        
        // When
        userTaskService.changeTaskStatus(taskId, (short) 5);
        
        // Then
        verify(taskRepository).save(task);
        assertThat(task.getStatus()).isEqualTo(doneStatus);
        assertThat(task.getCompletedAt()).isNotNull();
    }
    
    @Test
    void deleteTask_ShouldSoftDeleteTask() {
        // Given
        when(taskRepository.softDeleteTask(eq(taskId), eq(userId), any(ZonedDateTime.class))).thenReturn(1);
        
        // When
        userTaskService.deleteTask(taskId);
        
        // Then
        verify(taskRepository).softDeleteTask(eq(taskId), eq(userId), any(ZonedDateTime.class));
    }
    
    @Test
    void deleteTask_WhenTaskNotFound_ShouldThrowException() {
        // Given
        when(taskRepository.softDeleteTask(eq(taskId), eq(userId), any(ZonedDateTime.class))).thenReturn(0);
        
        // When & Then
        assertThatThrownBy(() -> userTaskService.deleteTask(taskId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Task not found");
    }
}