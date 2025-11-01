package com.myhealth.service;

import com.myhealth.dto.task.UserTaskCreateRequest;
import com.myhealth.entity.task.Priority;
import com.myhealth.entity.task.Status;
import com.myhealth.entity.task.Task;
import com.myhealth.impl.UserTaskServiceImpl;
import com.myhealth.projection.task.UserTaskDetailProjection;
import com.myhealth.repository.PriorityRepository;
import com.myhealth.repository.StatusRepository;
import com.myhealth.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTaskServiceJwtIntegrationTest {

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

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
    }

    @Test
    void createTask_ShouldCallGetLoggedInUserId() {
        // Given
        UserTaskCreateRequest request = new UserTaskCreateRequest();
        request.setTitle("Test Task");
        request.setPriorityId("3");

        Status todoStatus = new Status();
        todoStatus.setId((short) 2);

        Priority mediumPriority = new Priority();
        mediumPriority.setId((short) 3);

        Task savedTask = new Task();
        savedTask.setId(taskId);

        UserTaskDetailProjection detailProjection = mock(UserTaskDetailProjection.class);
        when(detailProjection.getId()).thenReturn(taskId);

        when(jwtTokenService.getLoggedInUserId()).thenReturn(userId);
        when(statusRepository.findById((short) 2)).thenReturn(Optional.of(todoStatus));
        when(priorityRepository.findById((short) 3)).thenReturn(Optional.of(mediumPriority));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskRepository.findUserTaskDetail(taskId, userId)).thenReturn(Optional.of(detailProjection));

        // When
        userTaskService.createTask(request);

        // Then
        verify(jwtTokenService, times(2)).getLoggedInUserId(); // Called in createTask and getUserTask
    }

    @Test
    void getUserTask_ShouldCallGetLoggedInUserId() {
        // Given
        UserTaskDetailProjection detailProjection = mock(UserTaskDetailProjection.class);
        when(detailProjection.getId()).thenReturn(taskId);

        when(jwtTokenService.getLoggedInUserId()).thenReturn(userId);
        when(taskRepository.findUserTaskDetail(taskId, userId)).thenReturn(Optional.of(detailProjection));

        // When
        userTaskService.getUserTask(taskId);

        // Then
        verify(jwtTokenService).getLoggedInUserId();
    }

    @Test
    void deleteTask_ShouldCallGetLoggedInUserId() {
        // Given
        when(jwtTokenService.getLoggedInUserId()).thenReturn(userId);
        when(taskRepository.softDeleteTask(eq(taskId), eq(userId), any())).thenReturn(1);

        // When
        userTaskService.deleteTask(taskId);

        // Then
        verify(jwtTokenService).getLoggedInUserId();
    }
}